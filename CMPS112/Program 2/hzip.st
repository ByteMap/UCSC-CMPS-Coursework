nl := Character nl.

FileStream extend [
   nl := Character nl.
   catFile [
      |count|
      count := 0.
      [self atEnd not] whileTrue: [
         count := count + 1.
         count asString size to: 5 do: [:skip| stdout << ' '].
         stdout << count << ': ' << self nextLine << nl.
      ]
   ]
]

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

]

Leaf subclass: Tree [
   |left right|

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

]
"======================================================================"

Smalltalk arguments: '-d -t -c -u'
stdout << 'Hello' << nl.
do: [:opt :arg | 
   (opt = $-c) ifTrue: [
      input := Smalltalk getArgv 2.
      output := Smalltalk getArgv 3.
      inputFile := FileStream open: input mode: FileStream read.
      outputFile := FileStream open: output mode: FileStream write.

      "Create The Frequency Table And Priority Queue"
      freqtable := ZeroArray new: 257.
      queue := SortedCollection new.

      "Store Value 1 At The Last Position Of The Table To Indicate EOF"
      freqtable at: 256 put: 1.

      "Count The Characters In The File And Store Into Table"
      [inputFile atEnd] whileFalse: [
         |character|
         character := inputFile next asInteger.
         freqtable incr: character.
      ].

      "Begin To Sort The Table Into A Priority Queue"
      freqtable keysAndValuesDo: [:key :value |
         (value notNil and: [value > 0]) ifTrue: [
            queue add: (Leaf new: key asCharacter value: value).
         ]
         stdout << 'freq[' << key asCharacter visible << ']=' << value
             << nl.
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
      encodetable := ZeroArray new: 257.

      "Extract The Fully Built Tree From The Priority Queue"
      huffTree := queue removeFirst.


   ].
].



