sig List {  header : lone Node }

sig Node {  link: lone Node }

pred Acyclic (l: List) {
no l.header or some n : Node | n in l.header.^link => no n.link
}

val ValidListSizeTwo {
some disj List0: List {some disj Node0, Node1: Node {
List = List0 
header = List0->Node0
Node = Node0 + Node1
link = Node0->Node1
@cmd: {Acyclic[List0]}
}}
}
@Test Test0: run ValidListSizeTwo for 3 expect 1

val mutantVal0 {
	some disj List0, List1: List {some disj Node0: Node {
		List = List0 + List1
		header = List1->Node0
		Node = Node0
		link = Node0->Node0
	}}
}
@Test mutantTest0: run mutantVal0 for 3 expect 1

val mutantVal1 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node2 + Node1->Node0
	}}
}
@Test mutantTest1: run mutantVal1 for 3 expect 1

val mutantVal2 {
	some disj Node0: Node {
		no List
		no header
		Node = Node0
		no link
	}
}
@Test mutantTest2: run mutantVal2 for 3 expect 1

val mutantVal3 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		header = List0->Node0 + List0->Node1 + List0->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node2 + Node1->Node1 + Node2->Node0
	}}
}
@Test mutantTest3: run mutantVal3 for 3 expect 0

val mutantVal4 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		no header
		Node = Node0 + Node1 + Node2
		link = Node0->Node2 + Node1->Node0
	}}
}
@Test mutantTest4: run mutantVal4 for 3 expect 1

val mutantVal5 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List0->Node2 + List1->Node1
		Node = Node0 + Node1 + Node2
		link = Node0->Node2 + Node1->Node0
	}}
}
@Test mutantTest5: run mutantVal5 for 3 expect 1

val mutantVal6 {
	
		no List
		no header
		no Node
		no link
	
}
@Test mutantTest6: run mutantVal6 for 3 expect 1

val mutantVal7 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List0->Node1 + List1->Node0
		Node = Node0 + Node1 + Node2
		link = Node2->Node0 + Node2->Node1 + Node2->Node2
	}}
}
@Test mutantTest7: run mutantVal7 for 3 expect 0

val mutantVal8 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List0->Node2 + List1->Node1
		Node = Node0 + Node1 + Node2
		link = Node0->Node1 + Node1->Node1
	}}
}
@Test mutantTest8: run mutantVal8 for 3 expect 1

val mutantVal9 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List0->Node2 + List1->Node1
		Node = Node0 + Node1 + Node2
		link = Node0->Node1 + Node0->Node2 + Node1->Node1 + Node2->Node0
	}}
}
@Test mutantTest9: run mutantVal9 for 3 expect 0

val mutantVal10 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node2 + Node1->Node0 + Node2->Node1
		@cmd:{ Acyclic[List1] }
	}}
}
@Test mutantTest10: run mutantVal10 for 3 expect 0

val mutantVal11 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node2 + Node1->Node0 + Node2->Node2
		@cmd:{ Acyclic[List1] }
	}}
}
@Test mutantTest11: run mutantVal11 for 3 expect 1

val mutantVal12 {
	some disj List0, List1: List {
		List = List0 + List1
		no header
		no Node
		no link
		@cmd:{ Acyclic[List1] }
	}
}
@Test mutantTest12: run mutantVal12 for 3 expect 1

val mutantVal13 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List0->Node2 + List1->Node1
		Node = Node0 + Node1 + Node2
		link = Node0->Node2 + Node1->Node0 + Node2->Node2
		@cmd:{ Acyclic[List1] }
	}}
}
@Test mutantTest13: run mutantVal13 for 3 expect 0

val mutantVal14 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		header = List0->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node2 + Node1->Node0 + Node2->Node1
		@cmd:{ Acyclic[List0] }
	}}
}
@Test mutantTest14: run mutantVal14 for 3 expect 0

val mutantVal15 {
	some disj List0, List1: List {some disj Node0: Node {
		List = List0 + List1
		header = List1->Node0
		Node = Node0
		link = Node0->Node0
		@cmd:{ Acyclic[List1] }
	}}
}
@Test mutantTest15: run mutantVal15 for 3 expect 0

val mutantVal16 {
	some disj List0, List1, List2: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1 + List2
		header = List0->Node2 + List1->Node2 + List2->Node1
		Node = Node0 + Node1 + Node2
		link = Node0->Node2 + Node1->Node0 + Node2->Node1
		@cmd:{ Acyclic[List2] }
	}}
}
@Test mutantTest16: run mutantVal16 for 3 expect 0

val mutantVal17 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		header = List0->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node2 + Node1->Node0 + Node2->Node2
		@cmd:{ Acyclic[List0] }
	}}
}
@Test mutantTest17: run mutantVal17 for 3 expect 1

val mutantVal18 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		header = List0->Node2
		Node = Node0 + Node1 + Node2
		link = Node2->Node2
		@cmd:{ Acyclic[List0] }
	}}
}
@Test mutantTest18: run mutantVal18 for 3 expect 1

val mutantVal19 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		header = List0->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node2 + Node1->Node1 + Node2->Node0
		@cmd:{ Acyclic[List0] }
	}}
}
@Test mutantTest19: run mutantVal19 for 3 expect 1

val mutantVal20 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		header = List0->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node1 + Node1->Node0 + Node2->Node1
		@cmd:{ Acyclic[List0] }
	}}
}
@Test mutantTest20: run mutantVal20 for 3 expect 0

val mutantVal21 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node1 + Node1->Node1 + Node2->Node0
		@cmd:{ Acyclic[List1] }
	}}
}
@Test mutantTest21: run mutantVal21 for 3 expect 0

val mutantVal22 {
	some disj List0, List1, List2: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1 + List2
		header = List0->Node2 + List1->Node2 + List2->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node1 + Node1->Node0 + Node2->Node1
		@cmd:{ Acyclic[List2] }
	}}
}
@Test mutantTest22: run mutantVal22 for 3 expect 0
