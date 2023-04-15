module unknown
open util/integer [] as integer
lone sig List {
header: (lone Node)
}
sig Node {
link: (lone Node)
}
pred Acyclic[l: List] {
((no (l.header)) || (some n: (one Node) {
((n in ((l.header).(^link))) => (no (n.link)))
}))
}
pred run_1[] {

}



run run_1

pred test0 {
some disj List0, List1: List {some disj Node0: Node {
List = List0 + List1
header = List1->Node0
Node = Node0
link = Node0->Node0
}}
}
run test0 for 3 expect 1

pred test1 {
some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
List = List0 + List1
header = List1->Node2
Node = Node0 + Node1 + Node2
link = Node0->Node2 + Node1->Node0
}}
}
run test1 for 3 expect 1

pred test2 {
some disj Node0: Node {
no List
no header
Node = Node0
no link
}
}
run test2 for 3 expect 1

pred test3 {
some disj List0: List {some disj Node0, Node1, Node2: Node {
List = List0
header = List0->Node0 + List0->Node1 + List0->Node2
Node = Node0 + Node1 + Node2
link = Node0->Node2 + Node1->Node1 + Node2->Node0
}}
}
run test3 for 3 expect 0

pred test4 {
some disj List0: List {some disj Node0, Node1, Node2: Node {
List = List0
no header
Node = Node0 + Node1 + Node2
link = Node0->Node2 + Node1->Node0
}}
}
run test4 for 3 expect 1

pred test5 {
some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
List = List0 + List1
header = List0->Node2 + List1->Node1
Node = Node0 + Node1 + Node2
link = Node0->Node2 + Node1->Node0
}}
}
run test5 for 3 expect 1

pred test6 {

no List
no header
no Node
no link

}
run test6 for 3 expect 1

pred test7 {
some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
List = List0 + List1
header = List0->Node1 + List1->Node0
Node = Node0 + Node1 + Node2
link = Node2->Node0 + Node2->Node1 + Node2->Node2
}}
}
run test7 for 3 expect 0

pred test8 {
some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
List = List0 + List1
header = List0->Node2 + List1->Node1
Node = Node0 + Node1 + Node2
link = Node0->Node1 + Node1->Node1
}}
}
run test8 for 3 expect 1

pred test9 {
some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
List = List0 + List1
header = List0->Node2 + List1->Node1
Node = Node0 + Node1 + Node2
link = Node0->Node1 + Node0->Node2 + Node1->Node1 + Node2->Node0
}}
}
run test9 for 3 expect 0

pred test10 {
some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
List = List0 + List1
header = List1->Node2
Node = Node0 + Node1 + Node2
link = Node0->Node2 + Node1->Node0 + Node2->Node1
Acyclic[List1]
}}
}
run test10 for 3 expect 0

pred test11 {
some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
List = List0 + List1
header = List1->Node2
Node = Node0 + Node1 + Node2
link = Node0->Node2 + Node1->Node0 + Node2->Node2
Acyclic[List1]
}}
}
run test11 for 3 expect 1

pred test12 {
some disj List0, List1: List {
List = List0 + List1
no header
no Node
no link
Acyclic[List1]
}
}
run test12 for 3 expect 1

pred test13 {
some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
List = List0 + List1
header = List0->Node2 + List1->Node1
Node = Node0 + Node1 + Node2
link = Node0->Node2 + Node1->Node0 + Node2->Node2
Acyclic[List1]
}}
}
run test13 for 3 expect 1

pred test14 {
some disj List0: List {some disj Node0, Node1, Node2: Node {
List = List0
header = List0->Node2
Node = Node0 + Node1 + Node2
link = Node0->Node2 + Node1->Node0 + Node2->Node1
Acyclic[List0]
}}
}
run test14 for 3 expect 0

pred test15 {
some disj List0, List1: List {some disj Node0: Node {
List = List0 + List1
header = List1->Node0
Node = Node0
link = Node0->Node0
Acyclic[List1]
}}
}
run test15 for 3 expect 0

pred test16 {
some disj List0, List1, List2: List {some disj Node0, Node1, Node2: Node {
List = List0 + List1 + List2
header = List0->Node2 + List1->Node2 + List2->Node1
Node = Node0 + Node1 + Node2
link = Node0->Node2 + Node1->Node0 + Node2->Node1
Acyclic[List2]
}}
}
run test16 for 3 expect 0

pred test17 {
some disj List0: List {some disj Node0, Node1, Node2: Node {
List = List0
header = List0->Node2
Node = Node0 + Node1 + Node2
link = Node0->Node2 + Node1->Node0 + Node2->Node2
Acyclic[List0]
}}
}
run test17 for 3 expect 1

pred test18 {
some disj List0: List {some disj Node0, Node1, Node2: Node {
List = List0
header = List0->Node2
Node = Node0 + Node1 + Node2
link = Node2->Node2
Acyclic[List0]
}}
}
run test18 for 3 expect 1

pred test19 {
some disj List0: List {some disj Node0, Node1, Node2: Node {
List = List0
header = List0->Node2
Node = Node0 + Node1 + Node2
link = Node0->Node2 + Node1->Node1 + Node2->Node0
Acyclic[List0]
}}
}
run test19 for 3 expect 1

pred test20 {
some disj List0: List {some disj Node0, Node1, Node2: Node {
List = List0
header = List0->Node2
Node = Node0 + Node1 + Node2
link = Node0->Node1 + Node1->Node0 + Node2->Node1
Acyclic[List0]
}}
}
run test20 for 3 expect 1

pred test21 {
some disj List0, List1: List {some disj Node0, Node1, Node2: Node {
List = List0 + List1
header = List1->Node2
Node = Node0 + Node1 + Node2
link = Node0->Node1 + Node1->Node1 + Node2->Node0
Acyclic[List1]
}}
}
run test21 for 3 expect 1

pred test22 {
some disj List0, List1, List2: List {some disj Node0, Node1, Node2: Node {
List = List0 + List1 + List2
header = List0->Node2 + List1->Node2 + List2->Node2
Node = Node0 + Node1 + Node2
link = Node0->Node1 + Node1->Node0 + Node2->Node1
Acyclic[List2]
}}
}
run test22 for 3 expect 1

