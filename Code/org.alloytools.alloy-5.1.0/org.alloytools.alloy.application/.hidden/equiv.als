module unknown
open util/integer [] as integer
sig List {
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

pred AcyclicMUTATED[l: List] {
((no (l.header)) || (some n: (one Node) {
((n in ((l.header).(^link))) => (no (n.(^link))))
}))
}
EQUIV: check { all l: List | Acyclic[l] <=> AcyclicMUTATED[l] } for 3