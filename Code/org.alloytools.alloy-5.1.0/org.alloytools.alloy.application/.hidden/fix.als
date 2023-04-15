module unknown
open util/integer [] as integer
sig Class {
ext: (lone Class)
}
one sig Object extends Class {}
pred ObjectNoExt[] {
(all c: (one Class) {
(Object !in (c.(^(~ext))))
})
}
pred AllExtObject[] {
(all c: (one Class) {
(Class = ((*ext).Object))
})
}
pred run_18[] {

}



run run_18
