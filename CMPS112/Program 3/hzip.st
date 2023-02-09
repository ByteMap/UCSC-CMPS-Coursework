#!/afs/cats.ucsc.edu/courses/cmps112-wm/usr/smalltalk/bin/gst -f
nl := Character nl.

"Object Subclass That Will Write Out Bits Into Output File"
Object subclass: OutBits [
   |bitIndex currentByte myStream|
   OutBits class >> new [
      self shouldNotImplement.
   ]
   OutBits class >> new: fileStream [
      |result|
      result := super new.
      result init: fileStream.
      ^result
   ]
   clearByte [
      bitIndex := 8.
      currentByte := 0.
   ]
   init: fileStream [
      myStream := fileStream.
      self clearByte.
   ]
   flushByte [
      bitIndex = 8 ifFalse: [
         myStream nextPutByte: currentByte.
         self clearByte.
      ]
   ]
   writeBit: bit [
      currentByte := currentByte bitAt: bitIndex put: bit.
      bitIndex := bitIndex - 1.
      bitIndex = 0 ifTrue: [self flushByte].
   ]
   writeASCII: char [
      8 to: 1 by: -1 do: [:bitIndex |
         self writeBit: ((char asInteger) bitAt: bitIndex).
      ].
   ]
]

"Object Subclass That Will Read In Bits From Input File"
Object subclass: BitStream [
   |bitIndex byte myStream|
   BitStream class >> new: fileStream [
      |result|
      result := super new.
      result init: fileStream.
      ^result
   ]
   init: fileStream [
      myStream := fileStream.
      bitIndex := 1.
   ]
   nextBit [
      bitIndex = 1 ifTrue: [
         byte := myStream next.
         bitIndex := 9.
      ].
      bitIndex := bitIndex - 1.
      ^byte value bitAt: bitIndex
   ]
   atEnd [
      ^bitIndex = 1 and: [myStream atEnd]
   ]
   currByte [
      ^byte
   ]
]

"Count Frequency Of Input Characters"
"Method To Check If The Character Is A 'Valid' Character"
Character extend [
   isPrint [
      ^ (Character space <= self) & (self <= $~)
   ]
   visible [
      self isPrint ifTrue: [^ '$', self asString]
                   ifFalse: [^ self asInteger printStringRadix: 8]
   ]
]

"Object Subclass That Creates The Frequency Table"
Object subclass: ZeroArray [
   |theArray|

   ZeroArray class >> new [
      self shouldNotImplement.
   ]

   ZeroArray class >> new: size [
      |result|
      result := super new.
      result init: size.
      ^ result
   ]

   init: size [
      theArray := Array new: size.
   ]

   size [
      ^ theArray size.
   ]

   at: index [
      ^ theArray at: index + 1.
   ]

   at: index put: value [
      ^ theArray at: index + 1 put: value.
   ]

   incr: index [
      (self at: index) isNil
         ifTrue: [ self at: index put: 0].
      self at: index put: (self at: index) + 1.
   ]

   keysAndValuesDo: aBlock [
      (0 to: (self size) - 1) do: [:index |
         aBlock value: index value: (self at: index).
      ]
   ]

]

"Object Class That Will Create The Huffman Tree"
Object subclass: Leaf [
   |char count|
   char [ ^ char ]
   count [ ^ count ]
   isALeaf [ ^ true ]

   Leaf class >> new [
      self shouldNotImplement
   ]

   Leaf class >> new: aChar count: aCount [
      |result|
      result := super new.
      result setChar: aChar andCount: aCount.
      ^result
   ]

   setChar: aChar andCount: aCount [
      char := aChar.
      count := aCount.
   ]

   <= other [
      ^ (count < other count)
      | ((count = other count) & (char <= other char))
   ]

   printBase: aStream [
      ^ aStream << self class << '(' << char << ',' << count
   ]

   printOn: aStream [
      (self printBase: aStream) << ')'.
   ]

   inorder: visitor prefix: string [
      visitor value: char value: string.
   ]
   writePostorder: outputFile [
      "Each Leaf Will Start With A Bit Of 0"
      outputFile writeBit: 0.
      outputFile writeASCII: char.
      char asInteger = 0 ifTrue: [
         outputFile writeBit: 1.
      ].
      char asInteger = 256 ifTrue: [
         outputFile writeBit: 0.
      ].
   ]
]

Leaf subclass: Tree [
   |left right|

   isALeaf [ ^ false ]

   Tree class >> new: aChar count: aCount [
      self shouldNotImplement
   ]

   Tree class >> new: aChar count: aCount left: aLeft right: aRight [
      |result|
      result := super new: aChar count: aCount.
      result setLeft: aLeft andRight: aRight.
      ^ result
   ]

   setLeft: aLeft andRight: aRight [
      left := aLeft.
      right := aRight.
   ]

   printOn: aStream [
      (self printBase: aStream) << ',' << left << ',' << right << ')'.
   ]

   inorder: visitor prefix: string [
      left inorder: visitor prefix: string, '0'.
      right inorder: visitor prefix: string, '1'.
   ]
   writePostorder: outputFile [
      "Post-Order Recursively Walks Left Then Right Then To The Parent"
      left writePostorder: outputFile.
      right writePostorder: outputFile.
      "Indicating That The Method Has Reached The End Of The Tree"
      outputFile writeBit: 1.
   ]
]
"======================================================================"
Smalltalk arguments: '-d -t -c -u'

do: [:opt :arg | 
   (opt = $d) ifTrue: [
      input := Smalltalk getArgv: 2.
      output := Smalltalk getArgv: 3.
      inputFile := FileStream open: input mode: FileStream read.
      inputFile2 := inputFile.
      output = nil ifTrue: [
         outputFile := stdout.
      ].
      output = nil ifFalse: [
         outputFile := FileStream open: output mode: FileStream write.
      ].
      outfile := OutBits new: outputFile.


      "Create The Frequency Table And Priority Queue"
      freqTable := ZeroArray new: 257.
      queue := SortedCollection new.

      "Store Value 1 At The Last Position Of The Table To Indicate EOF"
      freqTable at: 256 put: 1.

      "Count The Characters In The File And Store Into Table"
      [inputFile atEnd] whileFalse: [
         |character|
         character := inputFile next asInteger.
         freqTable incr: character.
      ].

      "Begin To Sort The Table Into A Priority Queue"
      freqTable keysAndValuesDo: [:key :value |
         (value notNil and: [value > 0]) ifTrue: [
            queue add: (Leaf new: key asCharacter count: value).
         ]
      ].

      "Begin To Build The Huffman Tree"
      "Until A Single Element Is Left (The Fully Built Tree)"
      [queue size > 1] whileTrue: [
         |leftChild rightChild sum|

         "Remove First Two Elements In Queue And Connect To Be A Tree"
         leftChild := queue removeFirst.
         rightChild := queue removeFirst.
         sum := (leftChild count) + (rightChild count).
         tree := (Tree new: (leftChild char) count: sum left: leftChild
                                                    right: rightChild).
         queue add: tree.
      ].
      huffTree := queue removeFirst.

      stdout << huffTree << nl.
   ].
   (opt = $t) ifTrue: [
      input := Smalltalk getArgv: 2.
      output := Smalltalk getArgv: 3.
      inputFile := FileStream open: input mode: FileStream read.
      inputFile2 := inputFile.
      output = nil ifTrue: [
         outputFile := stdout.
      ].
      output = nil ifFalse: [
         outputFile := FileStream open: output mode: FileStream write.
      ].
      outfile := OutBits new: outputFile.


      "Create The Frequency Table And Priority Queue"
      freqTable := ZeroArray new: 257.
      queue := SortedCollection new.

      "Store Value 1 At The Last Position Of The Table To Indicate EOF"
      freqTable at: 256 put: 1.

      "Count The Characters In The File And Store Into Table"
      [inputFile atEnd] whileFalse: [
         |character|
         character := inputFile next asInteger.
         freqTable incr: character.
      ].

      "Begin To Sort The Table Into A Priority Queue"
      freqTable keysAndValuesDo: [:key :value |
         (value notNil and: [value > 0]) ifTrue: [
            queue add: (Leaf new: key asCharacter count: value).
         ]
      ].

      "Begin To Build The Huffman Tree"
      "Until A Single Element Is Left (The Fully Built Tree)"
      [queue size > 1] whileTrue: [
         |leftChild rightChild sum|

         "Remove First Two Elements In Queue And Connect To Be A Tree"
         leftChild := queue removeFirst.
         rightChild := queue removeFirst.
         sum := (leftChild count) + (rightChild count).
         tree := (Tree new: (leftChild char) count: sum left: leftChild
                                                    right: rightChild).

         "Add The New Tree Into The Priority Queue"
         queue add: tree.
      ].

      "Create Encode Table"
      encodeTable := ZeroArray new: 257.

      "Extract The Fully Built Tree From The Priority Queue"
      huffTree := queue removeFirst.

      "Use Tree Inorder Method To Traverse Through The Tree And Write"
      "The Sequence Of Bits Indicating The Path From The Root To Leaves"
      huffTree inorder: [:char :string |
         encodeTable at: char asInteger put: string.
      ] prefix: ''.

      freqTable keysAndValuesDo: [:key :value |
      (value notNil and: [value > 0]) ifTrue: [
         |char|
         char := key asCharacter visible.
         stdout << char << '     '.
         stdout << value << ' '.
         stdout << (encodeTable at: key) << nl.

      ].
      ].
   ].
   (opt = $c) ifTrue: [
      input := Smalltalk getArgv: 2.
      output := Smalltalk getArgv: 3.
      inputFile := FileStream open: input mode: FileStream read.
      inputFile2 := inputFile.
      output = nil ifTrue: [
         outputFile := stdout.
      ].
      output = nil ifFalse: [
         outputFile := FileStream open: output mode: FileStream write.
      ].
      outfile := OutBits new: outputFile.


      "Create The Frequency Table And Priority Queue"
      freqTable := ZeroArray new: 257.
      queue := SortedCollection new.

      "Store Value 1 At The Last Position Of The Table To Indicate EOF"
      freqTable at: 256 put: 1.

      "Count The Characters In The File And Store Into Table"
      [inputFile atEnd] whileFalse: [
         |character|
         character := inputFile next asInteger.
         freqTable incr: character.
      ].

      "Begin To Sort The Table Into A Priority Queue"
      freqTable keysAndValuesDo: [:key :value |
         (value notNil and: [value > 0]) ifTrue: [
            queue add: (Leaf new: key asCharacter count: value).
         ]
      ].

      "Begin To Build The Huffman Tree"
      "Until A Single Element Is Left (The Fully Built Tree)"
      [queue size > 1] whileTrue: [
         |leftChild rightChild sum|

         "Remove First Two Elements In Queue And Connect To Be A Tree"
         leftChild := queue removeFirst.
         rightChild := queue removeFirst.
         sum := (leftChild count) + (rightChild count).
         tree := (Tree new: (leftChild char) count: sum left: leftChild
                                                    right: rightChild).

         "Add The New Tree Into The Priority Queue"
         queue add: tree.
      ].

      "Create Encode Table"
      encodeTable := ZeroArray new: 257.

      "Extract The Fully Built Tree From The Priority Queue"
      huffTree := queue removeFirst.

      "Use Tree Inorder Method To Traverse Through The Tree And Write"
      "The Sequence Of Bits Indicating The Path From The Root To Leaves"
      huffTree inorder: [:char :string |
         encodeTable at: char asInteger put: string.
      ] prefix: ''.

      "Perform A Post-Order Traversal Of The Tree, Writing Out One Bit"
      "At A Time In Big-Endian Format. Each Leaf Starts Off With 0 Bit"
      huffTree writePostorder: outfile.
      "Indicate EOF"
      outfile writeBit: 1.
      "Re-Open The Input File And Write Out The Encoded Version Of Each"
      "Byte"
      inputFile reset.
      [inputFile atEnd] whileFalse: [
         |character|
         character := inputFile next asInteger.
         encodeChar := encodeTable at: character.
         encodeChar do: [:bitIndex |
            bitIndex asString = '0' ifTrue: [
               outfile writeBit: 0.
            ].
            bitIndex asString = '1' ifTrue: [
               outfile writeBit: 1.
            ].
         ].
      ].
      "Write Out The Encoding String For EOF"
      EOF := encodeTable at: 256.
       EOF do: [:bitIndex |
         bitIndex asString = '0' ifTrue: [
            outfile writeBit: 0.
         ].
         bitIndex asString = '1' ifTrue: [
            outfile writeBit: 1.
         ].
      ].
      inputFile close.
      inputFile2 close.
      outputFile close.
   ].

   (opt = $u) ifTrue: [
      input := Smalltalk getArgv: 2.
      output := Smalltalk getArgv: 3.
      inputFile := FileStream open: input mode: FileStream read.
      output = nil ifTrue: [
         outputFile := stdout.
      ].
      output = nil ifFalse: [
         outputFile := FileStream open: output mode: FileStream write.
      ].
      inFile := BitStream new: inputFile.
      startBuild := true.
      dontEndFile := true.

      "Create The Stack"
      stack := OrderedCollection new. 

      "Read In The Input File And Reconstruct The Huffman Tree"
      [inputFile atEnd not & startBuild] whileTrue: [
         |bit|
         bit := inFile nextBit.
         "If The Tree Has Been Built, Stop Reading In The File And"
         "Building It. If Not, Then Continue Reading The Bits And"
         "Building The Tree"
         "Read One Bit, If It Is A 0 Then Read The Next 8 Bits"
         "And Reconstruct The Byte"
         startBuild ifTrue: [
            bit = 0 ifTrue: [
               reConstByte := 0.
               8 to: 1 by: -1 do: [:bitIndex |
                  reConstByte := reConstByte bitAt: bitIndex 
                                             put: inFile nextBit.
               ].
               leaf := Leaf new: reConstByte asCharacter count: 0.
               "If The Reconstructed Byte Has A Value Of 0, Read One More"
               "Bit"
               "If The Bit Is A 1, Add 256 To The Byte Indicating EOF"
               "Then Push The Byte Onto The Stack As A Leaf"
               reConstByte = 0 ifTrue: [
                  eofbit := inFile nextBit.
                  eofbit = 1 ifTrue: [
                     leaf := Leaf new: 256 count: 0.
                  ].
               ].
               stack addFirst: leaf.
            ].
            "If It Is A 1 Then Pop The 1-SubTree From The Stack, Then Pop"
            "The 0-SubTree From The Stack, And Create A New Tree With Both"
            "Of Those Children And Push The New Tree Back Onto The Stack"
            bit = 1 ifTrue: [
               "If There Is Only One Tree In The Stack, It Means This Is"
               "The Decoding Tree And The Process To Build The Tree Should"
               "Be Stopped"
               stack size > 1 ifTrue: [
                  rightSubTree := stack removeFirst.
                  leftSubTree := stack removeFirst.
                  tree := Tree new: (leftSubTree char) count: 0 
                               left: leftSubTree right: rightSubTree.
                  stack addFirst: tree.
               ]
               ifFalse: [
                  startBuild = false.
               ].
            ].
         ].
         "Remove The Tree From The Stack"
         (stack size ~= 0) ifTrue: [
            root := stack removeFirst.
            pointer := root.
         ].
         startBuild ifFalse: [
            bit := 0.
            [inPutFile atEnd not & dontEndFile] whileTrue: [
               bit := inFile nextBit.
               "If The Bit Is 0, Move DOwn The Left SubTree"
               bit = 0 ifTrue: [
                  pointer := pointer left.
               ]. 
               "If The Bit Is 1, Move Down The Right SubTree"
               bit = 1 ifTrue: [
                  pointer := pointer right.
               ].
               "If The Pointer Is A Leaf, Then Check If It Is EOF"
               "If Not, Print Out The Value Of The Leaf"
               pointer isALeaf ifTrue: [
                  node char = 256 ifTrue: [
                     dontEndFile := false.
                  ].
                  outputFile nextPut: pointer char asInteger.
               ].
            ].
         ].
      ].
      inputFile close.
      outputFile close.
   ].
].



