sig List {
    header : set Node
}

sig Node {
    link: set Node,
    elem: set Int
}

// Correct
fact CardinalityConstraints {
    all l: List | lone l.header
    all n: Node | lone n.link
    all n: Node | one n.elem
}

// Underconstraint.  Should disallow header = l1 -> n1, no link, Fix: replace "This.header.link" with "This.header".
pred Loop(This: List) {
    no This.header.link || one n: This.header.*link | n.link = n
}

// Overconstraint.  Should allow no n.link,  // Fix: replace "one n.link && n.elem <= n.link.elem" with "no n.link || n.elem <= n.link.elem".
pred Sorted(This: List) {
    all n: This.header.*link | one n.link && n.elem <= n.link.elem
}

pred RepOk(This: List) {
    Loop[This]
    Sorted[This]
}

val LoopListSize3 {
some disj List0: List {some disj Node0, Node1, Node2: Node {
List = List0
header = List0->Node2
Node = Node0 + Node1 + Node2
link = Node0->Node1 + Node2->Node0 + Node1->Node1
elem = Node0->2 + Node1->5 + Node2->1
Loop[List0]
}}
}
@Test Test0: run LoopListSize3 for 3 expect 1

val mutantVal0 {
	some disj List0, List1: List {some disj Node0, Node1: Node {
		List = List0 + List1
		header = List1->Node1
		Node = Node0 + Node1
		no link
		elem = Node0->6 + Node1->5
	}}
}
@Test mutantTest0: run mutantVal0 for 3 expect 1

val mutantVal1 {
	some disj List0, List1: List {some disj Node0: Node {
		List = List0 + List1
		header = List1->Node0
		Node = Node0
		no link
		elem = Node0->6
	}}
}
@Test mutantTest1: run mutantVal1 for 3 expect 1

val mutantVal2 {
	some disj Node0, Node1: Node {
		no List
		no header
		Node = Node0 + Node1
		no link
		elem = Node0->6 + Node1->5
	}
}
@Test mutantTest2: run mutantVal2 for 3 expect 1

val mutantVal3 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		no header
		Node = Node0 + Node1 + Node2
		no link
		elem = Node0->7 + Node1->6 + Node2->5
	}}
}
@Test mutantTest3: run mutantVal3 for 3 expect 1

val mutantVal4 {
	some disj List0: List {some disj Node0, Node1: Node {
		List = List0
		no header
		Node = Node0 + Node1
		no link
		elem = Node0->6 + Node1->5
	}}
}
@Test mutantTest4: run mutantVal4 for 3 expect 1

val mutantVal5 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List0->Node2 + List1->Node1
		Node = Node0 + Node1 + Node2
		no link
		elem = Node0->6 + Node1->2 + Node2->3
	}}
}
@Test mutantTest5: run mutantVal5 for 3 expect 1

val mutantVal6 {
	some disj List0, List1, List2: List {
		List = List0 + List1 + List2
		no header
		no Node
		no link
		no elem
	}
}
@Test mutantTest6: run mutantVal6 for 3 expect 1

val mutantVal7 {
	some disj List0, List1, List2: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1 + List2
		header = List0->Node1 + List1->Node1 + List2->Node0
		Node = Node0 + Node1 + Node2
		no link
		elem = Node0->6 + Node1->5 + Node2->6
	}}
}
@Test mutantTest7: run mutantVal7 for 3 expect 1

val mutantVal8 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List0->Node1 + List1->Node0
		Node = Node0 + Node1 + Node2
		no link
		elem = Node0->6 + Node1->5 + Node2->6
	}}
}
@Test mutantTest8: run mutantVal8 for 3 expect 1

val mutantVal9 {
	some disj List0, List1: List {some disj Node0, Node1: Node {
		List = List0 + List1
		header = List1->Node1
		Node = Node0 + Node1
		link = Node1->Node0
		elem = Node0->6 + Node1->5
		@cmd:{ Loop[List1] }
	}}
}
@Test mutantTest9: run mutantVal9 for 3 expect 0

val mutantVal10 {
	some disj List0, List1: List {some disj Node0, Node1: Node {
		List = List0 + List1
		header = List1->Node1
		Node = Node0 + Node1
		no link
		elem = Node0->6 + Node1->5
		@cmd:{ Loop[List1] }
	}}
}
@Test mutantTest10: run mutantVal10 for 3 expect 0

val mutantVal11 {
	some disj List0, List1: List {some disj Node0, Node1: Node {
		List = List0 + List1
		header = List1->Node1
		Node = Node0 + Node1
		link = Node1->Node1
		elem = Node0->6 + Node1->5
		@cmd:{ Loop[List1] }
	}}
}
@Test mutantTest11: run mutantVal11 for 3 expect 1

val mutantVal12 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node2
		elem = Node0->7 + Node1->6 + Node2->5
		@cmd:{ Loop[List1] }
	}}
}
@Test mutantTest12: run mutantVal12 for 3 expect 0

val mutantVal13 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node0 + Node1->Node0 + Node2->Node1
		elem = Node0->7 + Node1->6 + Node2->5
		@cmd:{ Loop[List1] }
	}}
}
@Test mutantTest13: run mutantVal13 for 3 expect 1

val mutantVal14 {
	some disj List0, List1: List {some disj Node0, Node1: Node {
		List = List0 + List1
		header = List1->Node1
		Node = Node0 + Node1
		link = Node0->Node0 + Node1->Node0
		elem = Node0->6 + Node1->5
		@cmd:{ Loop[List1] }
	}}
}
@Test mutantTest14: run mutantVal14 for 3 expect 1

val mutantVal15 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node0 + Node1->Node0 + Node2->Node1
		elem = Node0->6 + Node1->6 + Node2->5
		@cmd:{ Loop[List1] }
	}}
}
@Test mutantTest15: run mutantVal15 for 3 expect 1

val mutantVal16 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		link = Node1->Node0 + Node2->Node1
		elem = Node0->6 + Node1->6 + Node2->5
		@cmd:{ Loop[List1] }
	}}
}
@Test mutantTest16: run mutantVal16 for 3 expect 0

val mutantVal17 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node1 + Node2->Node0
		elem = Node0->7 + Node1->6 + Node2->5
		@cmd:{ Loop[List1] }
	}}
}
@Test mutantTest17: run mutantVal17 for 3 expect 0

val mutantVal18 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		no link
		elem = Node0->5 + Node1->5 + Node2->6
		@cmd:{ Sorted[List1] }
	}}
}
@Test mutantTest18: run mutantVal18 for 3 expect 1

val mutantVal19 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node0 + Node2->Node0
		elem = Node0->6 + Node1->3 + Node2->5
		@cmd:{ Sorted[List1] }
	}}
}
@Test mutantTest19: run mutantVal19 for 3 expect 1

val mutantVal20 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		link = Node1->Node0 + Node2->Node1
		elem = Node0->6 + Node1->3 + Node2->5
		@cmd:{ Sorted[List1] }
	}}
}
@Test mutantTest20: run mutantVal20 for 3 expect 0

val mutantVal21 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		no header
		Node = Node0 + Node1 + Node2
		link = Node2->Node2
		elem = Node0->7 + Node1->6 + Node2->5
		@cmd:{ Sorted[List1] }
	}}
}
@Test mutantTest21: run mutantVal21 for 3 expect 1

val mutantVal22 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		link = Node1->Node1 + Node2->Node1
		elem = Node0->2 + Node1->-3 + Node2->2
		@cmd:{ Sorted[List1] }
	}}
}
@Test mutantTest22: run mutantVal22 for 3 expect 0

val mutantVal23 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		link = Node1->Node2 + Node2->Node0
		elem = Node0->7 + Node1->5 + Node2->6
		@cmd:{ Sorted[List1] }
	}}
}
@Test mutantTest23: run mutantVal23 for 3 expect 1

val mutantVal24 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node2 + Node1->Node0 + Node2->Node1
		elem = Node0->6 + Node1->6 + Node2->5
		@cmd:{ Sorted[List1] }
	}}
}
@Test mutantTest24: run mutantVal24 for 3 expect 0

val mutantVal25 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		link = Node1->Node0 + Node2->Node1
		elem = Node0->-3 + Node1->-3 + Node2->-7
		@cmd:{ Sorted[List1] }
	}}
}
@Test mutantTest25: run mutantVal25 for 3 expect 1

val mutantVal26 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node2 + Node2->Node0
		elem = Node0->5 + Node1->5 + Node2->6
		@cmd:{ Sorted[List1] }
	}}
}
@Test mutantTest26: run mutantVal26 for 3 expect 0

val mutantVal27 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		link = Node1->Node2
		elem = Node0->2 + Node1->-1 + Node2->-3
		@cmd:{ Sorted[List1] }
	}}
}
@Test mutantTest27: run mutantVal27 for 3 expect 1

val mutantVal28 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		link = Node1->Node1 + Node2->Node2
		elem = Node0->6 + Node1->6 + Node2->5
		@cmd:{ Sorted[List1] }
	}}
}
@Test mutantTest28: run mutantVal28 for 3 expect 1

val mutantVal29 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node1 + Node1->Node0 + Node2->Node0
		elem = Node0->6 + Node1->6 + Node2->5
		@cmd:{ Sorted[List1] }
	}}
}
@Test mutantTest29: run mutantVal29 for 3 expect 1

val mutantVal30 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node2 + Node1->Node2 + Node2->Node0
		elem = Node0->6 + Node1->5 + Node2->6
		@cmd:{ Sorted[List1] }
	}}
}
@Test mutantTest30: run mutantVal30 for 3 expect 1

val mutantVal31 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node2 + Node2->Node0
		elem = Node0->6 + Node1->5 + Node2->6
		@cmd:{ Sorted[List1] }
	}}
}
@Test mutantTest31: run mutantVal31 for 3 expect 1

val mutantVal32 {
	some disj List0, List1: List {some disj Node0, Node1: Node {
		List = List0 + List1
		header = List1->Node1
		Node = Node0 + Node1
		link = Node0->Node0 + Node1->Node0
		elem = Node0->-4 + Node1->-5
		@cmd:{ Sorted[List1] }
	}}
}
@Test mutantTest32: run mutantVal32 for 3 expect 1

val mutantVal33 {
	some disj List0, List1: List {some disj Node0: Node {
		List = List0 + List1
		header = List1->Node0
		Node = Node0
		link = Node0->Node0
		elem = Node0->-1
		@cmd:{ Sorted[List1] }
	}}
}
@Test mutantTest33: run mutantVal33 for 3 expect 1

val mutantVal34 {
	some disj List0, List1: List {some disj Node0: Node {
		List = List0 + List1
		header = List1->Node0
		Node = Node0
		link = Node0->Node0
		elem = Node0->-3
		@cmd:{ Sorted[List1] }
	}}
}
@Test mutantTest34: run mutantVal34 for 3 expect 1

val mutantVal35 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List0->Node2 + List1->Node1
		Node = Node0 + Node1 + Node2
		link = Node0->Node1 + Node1->Node2 + Node2->Node2
		elem = Node0->6 + Node1->-3 + Node2->3
		@cmd:{ Sorted[List1] }
	}}
}
@Test mutantTest35: run mutantVal35 for 3 expect 1

val mutantVal36 {
	some disj List0, List1: List {some disj Node0, Node1: Node {
		List = List0 + List1
		header = List1->Node1
		Node = Node0 + Node1
		link = Node0->Node0 + Node1->Node0
		elem = Node0->6 + Node1->5
		@cmd:{ Sorted[List1] }
	}}
}
@Test mutantTest36: run mutantVal36 for 3 expect 1

val mutantVal37 {
	some disj List0, List1: List {some disj Node0: Node {
		List = List0 + List1
		header = List1->Node0
		Node = Node0
		link = Node0->Node0
		elem = Node0->-2
		@cmd:{ Sorted[List1] }
	}}
}
@Test mutantTest37: run mutantVal37 for 3 expect 1

val mutantVal38 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		header = List0->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node0 + Node1->Node2 + Node2->Node0
		elem = Node0->7 + Node1->5 + Node2->6
		@cmd:{ Sorted[List0] }
	}}
}
@Test mutantTest38: run mutantVal38 for 3 expect 1

val mutantVal39 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		header = List0->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node1 + Node1->Node1 + Node2->Node0
		elem = Node0->7 + Node1->5 + Node2->6
		@cmd:{ Sorted[List0] }
	}}
}
@Test mutantTest39: run mutantVal39 for 3 expect 0

val mutantVal40 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		header = List0->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node2 + Node1->Node0 + Node2->Node1
		elem = Node0->7 + Node1->5 + Node2->6
		@cmd:{ Sorted[List0] }
	}}
}
@Test mutantTest40: run mutantVal40 for 3 expect 0

val mutantVal41 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node2
		Node = Node0 + Node1 + Node2
		no link
		elem = Node0->7 + Node1->6 + Node2->5
		@cmd:{ RepOk[List1] }
	}}
}
@Test mutantTest41: run mutantVal41 for 3 expect 0

val mutantVal42 {
	some disj List0, List1: List {some disj Node0, Node1: Node {
		List = List0 + List1
		header = List1->Node1
		Node = Node0 + Node1
		link = Node0->Node1
		elem = Node0->6 + Node1->5
		@cmd:{ RepOk[List1] }
	}}
}
@Test mutantTest42: run mutantVal42 for 3 expect 0

val mutantVal43 {
	some disj List0, List1, List2: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1 + List2
		header = List1->Node2 + List2->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node2 + Node1->Node1 + Node2->Node0
		elem = Node0->5 + Node1->5 + Node2->5
		@cmd:{ RepOk[List2] }
	}}
}
@Test mutantTest43: run mutantVal43 for 3 expect 0

val mutantVal44 {
	some disj List0, List1: List {some disj Node0, Node1: Node {
		List = List0 + List1
		header = List1->Node1
		Node = Node0 + Node1
		no link
		elem = Node0->6 + Node1->5
		@cmd:{ RepOk[List1] }
	}}
}
@Test mutantTest44: run mutantVal44 for 3 expect 0

val mutantVal45 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		header = List0->Node0 + List0->Node1 + List0->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node0 + Node0->Node1 + Node0->Node2 + Node1->Node0 + Node1->Node1 + Node1->Node2 + Node2->Node0 + Node2->Node1 + Node2->Node2
		elem = Node1->-7 + Node1->-6 + Node1->-5 + Node1->-4 + Node1->-3 + Node1->-2 + Node1->-1 + Node1->0 + Node1->1 + Node1->2 + Node1->3 + Node1->4 + Node1->5 + Node1->6 + Node1->7 + Node2->-8 + Node2->7
	}}
}
@Test mutantTest45: run mutantVal45 for 3 expect 0

val mutantVal46 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		header = List0->Node0 + List0->Node1 + List0->Node2
		Node = Node0 + Node1 + Node2
		no link
		elem = Node0->4 + Node0->5 + Node0->6 + Node1->3 + Node2->-8 + Node2->-7 + Node2->-6 + Node2->-5 + Node2->-4 + Node2->-3 + Node2->-2 + Node2->-1 + Node2->0 + Node2->1 + Node2->2 + Node2->4 + Node2->5 + Node2->6
	}}
}
@Test mutantTest46: run mutantVal46 for 3 expect 0

val mutantVal47 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		header = List0->Node0 + List0->Node1 + List0->Node2
		Node = Node0 + Node1 + Node2
		no link
		elem = Node0->7 + Node1->6 + Node2->5
	}}
}
@Test mutantTest47: run mutantVal47 for 3 expect 0

val mutantVal48 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		no header
		Node = Node0 + Node1 + Node2
		link = Node0->Node0 + Node0->Node1 + Node1->Node0 + Node1->Node2
		elem = Node0->7 + Node1->5 + Node2->4
	}}
}
@Test mutantTest48: run mutantVal48 for 3 expect 0

val mutantVal49 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		no header
		Node = Node0 + Node1 + Node2
		no link
		elem = Node0->-5 + Node0->-3 + Node0->-2 + Node0->-1 + Node0->0 + Node0->1 + Node0->2 + Node0->3 + Node0->4 + Node0->5 + Node0->7 + Node1->-7 + Node1->-6 + Node1->-4 + Node1->6 + Node1->7 + Node2->-8 + Node2->-5 + Node2->-3 + Node2->-2 + Node2->-1 + Node2->0 + Node2->1 + Node2->2 + Node2->3 + Node2->4 + Node2->5 + Node2->6 + Node2->7
	}}
}
@Test mutantTest49: run mutantVal49 for 3 expect 0

val mutantVal50 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List1->Node0 + List1->Node1 + List1->Node2
		Node = Node0 + Node1 + Node2
		no link
		elem = Node0->7 + Node1->4 + Node2->3
	}}
}
@Test mutantTest50: run mutantVal50 for 3 expect 0

val mutantVal51 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List0->Node1 + List0->Node2 + List1->Node0
		Node = Node0 + Node1 + Node2
		no link
		elem = Node0->-4 + Node1->2 + Node2->1
	}}
}
@Test mutantTest51: run mutantVal51 for 3 expect 0

val mutantVal52 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		no header
		Node = Node0 + Node1 + Node2
		no link
		elem = Node0->6 + Node1->-4 + Node2->-5
	}}
}
@Test mutantTest52: run mutantVal52 for 3 expect 1

val mutantVal53 {
	some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
		List = List0 + List1
		header = List0->Node2 + List1->Node1
		Node = Node0 + Node1 + Node2
		no link
		elem = Node0->6 + Node1->0 + Node2->-1
	}}
}
@Test mutantTest53: run mutantVal53 for 3 expect 1

val mutantVal54 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		header = List0->Node2
		Node = Node0 + Node1 + Node2
		no link
		elem = Node0->7 + Node1->6 + Node2->5
	}}
}
@Test mutantTest54: run mutantVal54 for 3 expect 1

val mutantVal55 {
	some disj List0: List {
		List = List0
		no header
		no Node
		no link
		no elem
	}
}
@Test mutantTest55: run mutantVal55 for 3 expect 1

val mutantVal56 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		header = List0->Node2
		Node = Node0 + Node1 + Node2
		no link
		elem = Node0->6 + Node1->6 + Node2->5
	}}
}
@Test mutantTest56: run mutantVal56 for 3 expect 1

val mutantVal57 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		header = List0->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node2 + Node1->Node2 + Node2->Node0
		elem = Node0->7 + Node1->6 + Node2->5
	}}
}
@Test mutantTest57: run mutantVal57 for 3 expect 1

val mutantVal58 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		header = List0->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node2 + Node1->Node2 + Node2->Node2
		elem = Node0->7 + Node1->5 + Node2->6
	}}
}
@Test mutantTest58: run mutantVal58 for 3 expect 1

val mutantVal59 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		header = List0->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node2 + Node1->Node0 + Node2->Node1
		elem = Node0->6 + Node1->5 + Node2->6
	}}
}
@Test mutantTest59: run mutantVal59 for 3 expect 1

val mutantVal60 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		header = List0->Node2
		Node = Node0 + Node1 + Node2
		link = Node0->Node2 + Node1->Node0 + Node2->Node1
		elem = Node0->6 + Node1->6 + Node2->5
	}}
}
@Test mutantTest60: run mutantVal60 for 3 expect 1

val mutantVal61 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		no header
		Node = Node0 + Node1 + Node2
		no link
		elem = Node0->7 + Node1->5 + Node2->4
	}}
}
@Test mutantTest61: run mutantVal61 for 3 expect 1

val mutantVal62 {
	some disj List0: List {some disj Node0, Node1, Node2: Node {
		List = List0
		no header
		Node = Node0 + Node1 + Node2
		no link
		elem = Node0->6 + Node1->5 + Node2->4
	}}
}
@Test mutantTest62: run mutantVal62 for 3 expect 1

val mutantVal63 {
	some disj List0: List {some disj Node0: Node {
		List = List0
		no header
		Node = Node0
		no link
		no elem
	}}
}
@Test mutantTest63: run mutantVal63 for 3 expect 0

val mutantVal64 {
	some disj List0, List1, List2: List {some disj Node0, Node1: Node {
		List = List0 + List1 + List2
		header = List0->Node1 + List1->Node1 + List2->Node1
		Node = Node0 + Node1
		no link
		elem = Node0->1 + Node0->7 + Node1->-2
	}}
}
@Test mutantTest64: run mutantVal64 for 3 expect 0

