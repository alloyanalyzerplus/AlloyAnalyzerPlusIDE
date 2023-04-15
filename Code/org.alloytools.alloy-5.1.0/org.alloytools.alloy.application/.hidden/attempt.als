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
((n in ((l.header).(^link))) => (no (n.(^link))))
}))
}
pred run_1[] {

}



run run_1
