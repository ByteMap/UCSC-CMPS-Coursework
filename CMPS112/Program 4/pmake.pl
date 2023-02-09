#!/usr/bin/perl
# $Id: graph.perl,v 1.1 2018-11-06 18:50:43-08 - - $

use strict;
use warnings;
use Getopt::Std;
use Data::Dumper;
use POSIX qw(strftime);
$0 =~ s|.*/||;


my %tmpHashCmd;
my $targSet = 0;
my $tmp_string = "";
my $buildTarget;
my $validatedDeps;
my @macros = ();
my @targ_dep = ();
my @commands = ();

my $filename = 'Makefile';
open my $file, $filename or die "Could not open $filename: $!";

while(my $line = <$file>) {
   #If the line is a comment or an empty line, then skip it
   next if ($line =~ m/^#/) || ($line =~ m/^$/);
   my $firstChar = substr($line, 0, 1);
   #If the line contains an '=' sign without being a line indicating
   #target:prerequisite, then that line contains a macro that is then
   #added to the macro array.
   if (($line =~ /=/) && ($line !~ /\t/)) {
      push(@macros, $line);
   }
   #If the line contains the ':' character, then the line indicates it
   #is a target:prerequisite line and it will be pushed into the
   #targ_dep array
   elsif ($line =~ /:/) {
      push(@targ_dep, $line);
      my @string_elements = split /:/, $line;
      $tmp_string = $string_elements[0];
      $tmp_string = substr ($tmp_string, 0, length ($tmp_string) - 1);
      #print $tmp_string;
      #print ("\n");
   }
   #If the line contains a tab, then the line indicates it is a command
   #and it will be pushed into the commands array
   elsif ($line =~ /\t/) {
      $line =~ s/^\t//;
      $tmpHashCmd{$tmp_string} = $line;
      #print $tmpHashCmd{$tmp_string};
   }
}

#print @macros;
#print @targ_dep;
#print @commands;

sub macro_parse_dep ($) {
   my ($line) = @_;
   return undef unless $line =~ m/^(\S+)\s*=\s*(.*?)\s*$/;
   my ($mac, $macroDependency) = ($1, $2);
   my @macroDependencies = split m/\s+/, $macroDependency;
   return $mac, \@macroDependencies;
}

my %hashMacro;
for my $macroInput (@macros) {
   my ($mac, $macroDeps) = macro_parse_dep $macroInput;
   print "$0: syntax error: $macroInput\n" and next unless defined $mac;
   $hashMacro{$mac} = $macroDeps;
}

for my $macKey (keys %hashMacro) {
   my $temp = "@{$hashMacro{$macKey}}";
   my @temp_elements = split / /, $temp;
   #print $temp;
   #print ("\n");
   foreach (@temp_elements) {
      if (substr ($_, 0, 1) eq "\$") {
         #print @temp_elements;
         #print ("\n");
         my $temp_key = substr ($_, 2, length ($_) - 3);
         $_ = "@{$hashMacro{$temp_key}}";
         #print $_;
      }
   }
   $hashMacro{$macKey} = [@temp_elements];
   #print Dumper \@temp_elements;
}

my %hashCmd;
for my $cmdKey (keys %tmpHashCmd) {
   my $cmdVal = $tmpHashCmd{$cmdKey};
   #print $cmdVal;
   #print ("\n");
   my @tempCmd_elements = split /\s+/, $cmdVal;
   #print @tempCmd_elements;
   foreach (@tempCmd_elements) {
      if (substr ($_, 0, 2) eq "\${") {
         #print $_;
         #print ("\n");
         my $tempCmd_key = substr ($_, 2, length ($_) - 3);
         $_ = "@{$hashMacro{$tempCmd_key}}";
      }
      elsif (substr ($_, 0, 4) eq "\./\${") {
         #print $_;
         my $tempCmd_key = substr ($_, 4, length ($_) - 5);
         $_ = "@{$hashMacro{$tempCmd_key}}";
         #print $_;
      }
   }
   my $newCmds = join (' ', @tempCmd_elements);
   if (substr ($cmdKey, 0, 1) eq "\$") {
      $cmdKey = substr ($cmdKey, 2, length ($cmdKey) - 3);
      $cmdKey = "@{$hashMacro{$cmdKey}}";
   }
   $hashCmd{$cmdKey} = $newCmds;
}

sub targ_parse_dep ($) {
   my ($line) = @_;
   return undef unless $line =~ m/^(\S+)\s*:\s*(.*?)\s*$/;
   my ($targ, $targDep) = ($1, $2);
   my @targDeps = split m/\s+/, $targDep;
   if (substr ($targ, 0 ,1) eq "\$") {
      my $mac_key = substr ($targ, 2, length($targ) - 3);
      #print @{$hashMacro{$mac_key}};
      $targ = "@{$hashMacro{$mac_key}}";
      #print $targ;
   }
   foreach (@targDeps) {
      if (substr ($_, 0, 1) eq "\$") {
         my $mac_key2 = substr ($_, 2, length($_) - 3);
         $_ = "@{$hashMacro{$mac_key2}}";
      }
   }
   return $targ, \@targDeps;
}

my %hashTarg;
for my $targInput (@targ_dep) {
   my ($targ, $targDeps) = targ_parse_dep $targInput;
   if ($targSet == 0) {
      $buildTarget = $targ;
      $targSet = 1;
   }
   print "$0: syntax error: $targInput\n" and next unless defined $targ;
   $hashTarg{$targ} = $targDeps;
}

sub mtime ($) {
   my ($filename) = @_;
   my @stat = stat $filename;
   return @stat ? $stat[9] : undef;
}

sub execute_command {
   my $targ = shift;
   my $targDeps = "@{$hashTarg{$targ}}";
   #print $targ;
   #print (" ");
   #print $targDeps;
   #print ("\n");
   #If the target has no dependencies (i.e clean), then just execute the
   #command.
   if (!defined $targDeps) {
      system("$hashCmd{$targ}");
   }
   else {
      my @allTargDeps = split / /, $targDeps;
      foreach my $toValidate (@allTargDeps) {
         $validatedDeps = validate_deps($targ, $toValidate);
         #print "@{$hashTarg{$toValidate}}";
         #@allTargDeps If the signal is a 1, the new $targ is the
         #element of and run that target through validate_deps.
         if ($validatedDeps == 1) {
            #print $targ;
            #print $targ;
            #print " ";
            #print $toValidate;
            #print ("\n");
            execute_command($toValidate);
            #Revalidate the deps after recursion builds the dep files
            #that are missing or outdated.
            #$validatedDeps = validate_deps($targ, $toValidate);
         }
         #If the signal is a 2, begin the process of executing the
         #command that is linked to the $targ via %hashCmd.
         if ($validatedDeps == 2) {
            if (defined $hashCmd{$targ}) {
               my $command = "$hashCmd{$targ}";
               system($command);
            }
            #print $targ;
            #print " ";
            #print $toValidate;
            #print ("\n");
            #system($command);
         }
      }
      #print @allTargDeps;
   }
}

#This subroutine not only compares the time between two files, but also
#checks if the target or a dependency element is a file or not.
sub validate_deps ($) {
   my $cmp1 = shift;
   my $cmp2 = shift;
   #print $cmp2;
   #print ("\n");
   #If neither $cmp1 nor $cmp2 is a file, then return 1, which means to
   #recursively get the next element for $cmp2.
   if ((! -e $cmp1) && (! -e $cmp2)) {
      return 1;
   }
   #Else if $cmp1 is not a file but $cmp2 is a file, then return 2,
   #which means to just execute the command.
   elsif ((! -e $cmp1) && (-e $cmp2)) {
         #print $cmp1;
         #print " ";
         #print $cmp2;
         #print ("\n");
         #print ("\n");
         return 2;
   }
   #Else both $cmp1 and $cmp2 are files and will undergo comparing the 
   #timestamp of both files
   else {
      my $mtime1 = mtime $cmp1;
      my $mtime2 = mtime $cmp2;
      #If the timestamp of $cmp1 is less than that of $cmp2, then return
      #2 which is an indicator to rebuild $cmp1 and keep it updated.
      if ($mtime1 < $mtime2) {
         return 2;
      }
      #Else, everything is up to date and 3 is returned, which indicates
      #that $cmp1 is already up to date and does not require to be 
      #rebuilt.
      else {
         return 3;
      }

   }
}

execute_command($buildTarget);
#print @{$hashMacro{'CHECKIN'}};
#print Dumper (\%hashMacro);
#print Dumper (\%hashTarg);
print Dumper (\%hashCmd);
#print Dumper (\%tmpHashCmd);
#print $targ_dep[0];
#print "$hashCmd{'hello'}";
close $file;



