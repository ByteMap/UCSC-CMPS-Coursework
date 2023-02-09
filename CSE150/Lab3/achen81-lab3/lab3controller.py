# Lab 4 Skeleton
#
# Based on of_tutorial by James McCauley
# Worked with Josh Rony, Lang Li, and Ryan Haque

from pox.core import core
import pox.openflow.libopenflow_01 as of

log = core.getLogger()

class Firewall (object):
  """
  A Firewall object is created for each switch that connects.
  A Connection object for that switch is passed to the __init__ function.
  """
  def __init__ (self, connection):
    # Keep track of the connection to the switch so that we can
    # send it messages!
    self.connection = connection

    # This binds our PacketIn event listener
    connection.addListeners(self)

  # Algorithm should look like this:
  # For each packet from the switch:
  # 1) Get the packet's protocol
  # 2) Is protocol TCP?
  #    Yes: Create a rule, push the packet into the rule, then push that rule to the switch so the switch can remember what to do when the same packet is encountered
  #         Accept the packet and pass between all hosts => FLOOD packet
  #         DONE
  #    DONE
  # 3) Is protocol ARP?
  #    Yes: Create a rule, push the packet into the rule, then push that rule to the switch so the switch can remember what to do when the same packet is encountered
  #         Accept the packet and pass between all hosts => FLOOD packet
  #         DONE
  # 4) Is protocol ICMP?
  #    Yes: Is source IP 10.0.1.40/24 and destination IP 10.0.1.10/24? ||
  #         Is source IP 10.0.1.10/24 and destination IP 10.0.1.40/24?
  #         Yes: Create a rule, push the packet into the rule, then push that rule to the switch so the switch can remember what to do when the same packet is encountered
  #              Accept the packet and pass between all hosts involved => FLOOD packet
  #              DONE
  #    No : Drop packet (drops by default in timeout)
  #         DONE
  # 5) Is packet not being matched by any of the above rules?
  #    Yes: Drop packet (drops by default in timeout)
  #         DONE
  def do_firewall (self, packet, packet_in):
    def flood():
      print(packet)
      rule = of.ofp_flow_mod()
      #rule.match.nw_src   = packet.find('ipv4').srcip
      #rule.match.nw_dst   = packet.find('ipv4').dstip
      #rule.match.nw_proto = packet.find('ipv4').protocol
      print(packet.find('ipv4'))
      rule.match = of.ofp_match.from_packet(packet)
      rule.idle_timeout = 70
      rule.hard_timeout = 100
      rule.actions.append(of.ofp_action_output(port = of.OFPP_FLOOD))
      self.connection.send(rule)

      message = of.ofp_packet_out(data = packet_in)
      message.actions.append(of.ofp_action_output(port = of.OFPP_FLOOD))
      self.connection.send(message)
    
    TCP  = packet.find('tcp')
    ARP  = packet.find('arp')
    ICMP = packet.find('icmp')
    if TCP:
      print("TCP")
      flood()

    elif ARP:
      print("ARP")
      flood()

    elif ICMP:  
      ip = packet.find('ipv4')
      if ip.srcip == "10.0.1.10" and ip.dstip == "10.0.1.40":
        print("ICMP")
        flood()
      elif ip.srcip == "10.0.1.40" and ip.dstip == "10.0.1.10":
        print("ICMP")
        flood()

  def _handle_PacketIn (self, event):
    """
    Handles packet in messages from the switch.
    """

    packet = event.parsed # This is the parsed packet data.
    if not packet.parsed:
      log.warning("Ignoring incomplete packet")
      return

    packet_in = event.ofp # The actual ofp_packet_in message.
    self.do_firewall(packet, packet_in)

def launch ():
  """
  Starts the component
  """
  def start_switch (event):
    log.debug("Controlling %s" % (event.connection,))
    Firewall(event.connection)
  core.openflow.addListenerByName("ConnectionUp", start_switch)
