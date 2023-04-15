sig Element {}

one sig Array {
  // Maps indexes to elements of Element.
  i2e: Int -> Element,
  // Represents the length of the array.
  length: Int
}

// Assume all objects are in the array.
fact Reachable {
  Element = Array.i2e[Int]
}

// Part (a)
fact InBound {
  // All indexes should be greater than or equal to 0 and less than
  // the array length.
  // Fix: replace > with >=
  all i:Element.~(Array.i2e) | i > 0 && i < Array.length

  // Array length should be greater than or equal to 0.
  Array.length >= 0
}

// Part (b)
pred NoConflict() {
  // Each index maps to at most one element.
  // the line below solves the problem as it is written:
  all idx1, idx2: Int, a1: Array.i2e[idx1], a2: Array.i2e[idx2] | a1 != a2 => idx1 != idx2

  // The line below solves the problem: "Each index maps to at most one UNIQUE element." if that is what was intended
  //all idx1, idx2: Int, a1: Array.i2e[idx1], a2: Array.i2e[idx2] | (a1 != a2 => idx1 != idx2) and (idx1 != idx2 => a1 != a2)
}

run NoConflict for 5

val EmptyArr {
some disj Array0: Array {
no Element
Array = Array0
no i2e
length = Array0->0
}}
@Test Test0: run EmptyArr for 3 expect 1

val mutantVal0 {
	some disj Element0, Element1: Element {some disj Array0: Array {
		Element = Element0 + Element1
		Array = Array0
		i2e = Array0->1->Element1 + Array0->2->Element0 + Array0->3->Element0 + Array0->4->Element0 + Array0->5->Element0 + Array0->6->Element0
		length = Array0->7
	}}
}
@Test mutantTest0: run mutantVal0 for 3 expect 1

val mutantVal1 {
	some disj Element0, Element1: Element {some disj Array0: Array {
		Element = Element0 + Element1
		Array = Array0
		i2e = Array0->1->Element1 + Array0->2->Element0 + Array0->3->Element1 + Array0->6->Element0
		length = Array0->7
	}}
}
@Test mutantTest1: run mutantVal1 for 3 expect 1

val mutantVal2 {
	some disj Array0: Array {
		no Element
		Array = Array0
		no i2e
		length = Array0->0
	}
}
@Test mutantTest2: run mutantVal2 for 3 expect 1

val mutantVal3 {
	
		no Element
		no Array
		no i2e
		no length
	
}
@Test mutantTest3: run mutantVal3 for 3 expect 0

val mutantVal4 {
	some disj Element0, Element1, Element2: Element {some disj Array0, Array1: Array {
		Element = Element0 + Element1 + Element2
		Array = Array0 + Array1
		i2e = Array0->1->Element0 + Array0->1->Element1 + Array0->1->Element2 + Array0->2->Element0 + Array0->2->Element1 + Array0->2->Element2 + Array0->3->Element0 + Array0->3->Element1 + Array0->3->Element2 + Array0->4->Element1 + Array0->4->Element2 + Array0->5->Element2 + Array0->6->Element1 + Array1->1->Element0 + Array1->1->Element2 + Array1->2->Element1 + Array1->3->Element1 + Array1->4->Element0 + Array1->4->Element2
		length = Array0->7 + Array1->0
	}}
}
@Test mutantTest4: run mutantVal4 for 3 expect 0

val mutantVal5 {
	some disj Element0, Element1, Element2: Element {some disj Array0, Array1, Array2: Array {
		Element = Element0 + Element1 + Element2
		Array = Array0 + Array1 + Array2
		i2e = Array0->1->Element1 + Array0->1->Element2 + Array0->3->Element0 + Array0->3->Element2 + Array1->1->Element0 + Array1->2->Element0 + Array1->2->Element2 + Array1->3->Element1 + Array1->4->Element1 + Array1->5->Element1 + Array2->1->Element1 + Array2->1->Element2 + Array2->2->Element1 + Array2->3->Element0 + Array2->3->Element2 + Array2->4->Element0 + Array2->4->Element2 + Array2->5->Element0 + Array2->5->Element2
		length = Array0->7 + Array1->7 + Array2->-1
	}}
}
@Test mutantTest5: run mutantVal5 for 3 expect 0

val mutantVal6 {
	some disj Element0, Element1: Element {some disj Array0: Array {
		Element = Element0 + Element1
		Array = Array0
		i2e = Array0->1->Element0 + Array0->1->Element1
		length = Array0->-1 + Array0->0 + Array0->1 + Array0->2 + Array0->3
	}}
}
@Test mutantTest6: run mutantVal6 for 3 expect 0

val mutantVal7 {
	some disj Array0: Array {
		no Element
		Array = Array0
		no i2e
		no length
	}
}
@Test mutantTest7: run mutantVal7 for 3 expect 0

val mutantVal8 {
	some disj Element0, Element1: Element {some disj Array0: Array {
		Element = Element0 + Element1
		Array = Array0
		i2e = Array0->1->Element1 + Array0->2->Element0
		length = Array0->0 + Array0->3
	}}
}
@Test mutantTest8: run mutantVal8 for 3 expect 0

val mutantVal9 {
	some disj Element0, Element1, Element2: Element {some disj Array0: Array {
		Element = Element0 + Element1 + Element2
		Array = Array0
		i2e = Array0->1->Element2 + Array0->2->Element1 + Array0->3->Element1 + Array0->4->Element1 + Array0->5->Element1 + Array0->6->Element0 + Array0->6->Element1
		length = Array0->7
		@cmd:{ NoConflict[] }
	}}
}
@Test mutantTest9: run mutantVal9 for 3 expect 0

val mutantVal10 {
	some disj Element0, Element1: Element {some disj Array0: Array {
		Element = Element0 + Element1
		Array = Array0
		i2e = Array0->1->Element1 + Array0->2->Element0
		length = Array0->3
		@cmd:{ NoConflict[] }
	}}
}
@Test mutantTest10: run mutantVal10 for 3 expect 1

val mutantVal11 {
	some disj Element0: Element {some disj Array0: Array {
		Element = Element0
		Array = Array0
		i2e = Array0->2->Element0 + Array0->3->Element0
		length = Array0->4
		@cmd:{ NoConflict[] }
	}}
}
@Test mutantTest11: run mutantVal11 for 3 expect 1

val mutantVal12 {
	some disj Element0, Element1: Element {some disj Array0: Array {
		Element = Element0 + Element1
		Array = Array0
		i2e = Array0->1->Element1 + Array0->2->Element0
		length = Array0->4
		@cmd:{ NoConflict[] }
	}}
}
@Test mutantTest12: run mutantVal12 for 3 expect 1

val mutantVal13 {
	some disj Array0: Array {
		no Element
		Array = Array0
		no i2e
		length = Array0->1
		@cmd:{ NoConflict[] }
	}
}
@Test mutantTest13: run mutantVal13 for 3 expect 1

val mutantVal14 {
	some disj Element0, Element1, Element2: Element {some disj Array0: Array {
		Element = Element0 + Element1 + Element2
		Array = Array0
		i2e = Array0->1->Element2 + Array0->2->Element1 + Array0->4->Element0 + Array0->5->Element0
		length = Array0->7
		@cmd:{ NoConflict[] }
	}}
}
@Test mutantTest14: run mutantVal14 for 3 expect 1

val mutantVal15 {
	some disj Element0, Element1: Element {some disj Array0: Array {
		Element = Element0 + Element1
		Array = Array0
		i2e = Array0->2->Element0 + Array0->2->Element1
		length = Array0->3
		@cmd:{ NoConflict[] }
	}}
}
@Test mutantTest15: run mutantVal15 for 3 expect 0

val mutantVal16 {
	some disj Element0: Element {some disj Array0: Array {
		Element = Element0
		Array = Array0
		i2e = Array0->-8->Element0 + Array0->-7->Element0 + Array0->-6->Element0 + Array0->-5->Element0 + Array0->-4->Element0 + Array0->-3->Element0 + Array0->-2->Element0 + Array0->-1->Element0 + Array0->0->Element0 + Array0->1->Element0 + Array0->2->Element0 + Array0->3->Element0 + Array0->4->Element0 + Array0->5->Element0 + Array0->6->Element0 + Array0->7->Element0
		length = Array0->-8
	}}
}
@Test mutantTest16: run mutantVal16 for 3 expect 0

val mutantVal17 {
	some disj Array0: Array {
		no Element
		Array = Array0
		no i2e
		length = Array0->-1
	}
}
@Test mutantTest17: run mutantVal17 for 3 expect 0

val mutantVal18 {
	some disj Element0: Element {some disj Array0: Array {
		Element = Element0
		Array = Array0
		i2e = Array0->-8->Element0 + Array0->-7->Element0 + Array0->-6->Element0 + Array0->-5->Element0 + Array0->-4->Element0 + Array0->-3->Element0 + Array0->-2->Element0 + Array0->-1->Element0 + Array0->0->Element0 + Array0->7->Element0
		length = Array0->0
	}}
}
@Test mutantTest18: run mutantVal18 for 3 expect 0

val mutantVal19 {
	some disj Array0: Array {
		no Element
		Array = Array0
		no i2e
		length = Array0->-6
	}
}
@Test mutantTest19: run mutantVal19 for 3 expect 0

val mutantVal20 {
	some disj Element0: Element {some disj Array0: Array {
		Element = Element0
		Array = Array0
		i2e = Array0->-8->Element0 + Array0->-7->Element0 + Array0->-6->Element0 + Array0->-5->Element0 + Array0->-4->Element0 + Array0->-3->Element0 + Array0->-2->Element0 + Array0->-1->Element0 + Array0->0->Element0 + Array0->1->Element0 + Array0->2->Element0 + Array0->3->Element0 + Array0->4->Element0 + Array0->5->Element0 + Array0->6->Element0 + Array0->7->Element0
		length = Array0->1
	}}
}
@Test mutantTest20: run mutantVal20 for 3 expect 0

val mutantVal21 {
	some disj Element0: Element {some disj Array0: Array {
		Element = Element0
		Array = Array0
		i2e = Array0->-8->Element0 + Array0->-7->Element0 + Array0->-6->Element0 + Array0->-5->Element0 + Array0->-4->Element0 + Array0->-3->Element0 + Array0->-2->Element0 + Array0->-1->Element0 + Array0->0->Element0 + Array0->1->Element0 + Array0->2->Element0 + Array0->3->Element0 + Array0->4->Element0 + Array0->5->Element0 + Array0->6->Element0 + Array0->7->Element0
		length = Array0->2
	}}
}
@Test mutantTest21: run mutantVal21 for 3 expect 0

val mutantVal22 {
	some disj Element0: Element {some disj Array0: Array {
		Element = Element0
		Array = Array0
		i2e = Array0->-8->Element0 + Array0->-7->Element0 + Array0->-6->Element0 + Array0->-5->Element0 + Array0->-4->Element0 + Array0->-3->Element0 + Array0->-2->Element0 + Array0->-1->Element0 + Array0->0->Element0 + Array0->1->Element0 + Array0->2->Element0 + Array0->3->Element0 + Array0->4->Element0 + Array0->5->Element0 + Array0->6->Element0 + Array0->7->Element0
		length = Array0->6
	}}
}
@Test mutantTest22: run mutantVal22 for 3 expect 0

val mutantVal23 {
	some disj Element0: Element {some disj Array0: Array {
		Element = Element0
		Array = Array0
		i2e = Array0->-8->Element0 + Array0->-7->Element0 + Array0->-6->Element0 + Array0->-5->Element0 + Array0->-4->Element0 + Array0->-3->Element0 + Array0->-2->Element0 + Array0->-1->Element0
		length = Array0->0
	}}
}
@Test mutantTest23: run mutantVal23 for 3 expect 0

val mutantVal24 {
	some disj Element0: Element {some disj Array0: Array {
		Element = Element0
		Array = Array0
		i2e = Array0->6->Element0
		length = Array0->0
	}}
}
@Test mutantTest24: run mutantVal24 for 3 expect 0

val mutantVal25 {
	some disj Element0: Element {some disj Array0: Array {
		Element = Element0
		Array = Array0
		i2e = Array0->3->Element0
		length = Array0->4
	}}
}
@Test mutantTest25: run mutantVal25 for 3 expect 1

val mutantVal26 {
	some disj Element0, Element1: Element {some disj Array0: Array {
		Element = Element0 + Element1
		Array = Array0
		i2e = Array0->-1->Element0 + Array0->-1->Element1
		length = Array0->0
	}}
}
@Test mutantTest26: run mutantVal26 for 3 expect 0

val mutantVal27 {
	some disj Element0, Element1: Element {some disj Array0: Array {
		Element = Element0 + Element1
		Array = Array0
		i2e = Array0->1->Element1 + Array0->2->Element1 + Array0->3->Element1 + Array0->4->Element1 + Array0->5->Element0 + Array0->6->Element1
		length = Array0->7
	}}
}
@Test mutantTest27: run mutantVal27 for 3 expect 1

val mutantVal28 {
	some disj Element0, Element1: Element {some disj Array0: Array {
		Element = Element0 + Element1
		Array = Array0
		i2e = Array0->1->Element1 + Array0->2->Element1 + Array0->3->Element1 + Array0->4->Element1 + Array0->5->Element0 + Array0->6->Element0
		length = Array0->7
	}}
}
@Test mutantTest28: run mutantVal28 for 3 expect 1

val mutantVal29 {
	some disj Element0: Element {some disj Array0: Array {
		Element = Element0
		Array = Array0
		i2e = Array0->0->Element0 + Array0->1->Element0
		length = Array0->2
	}}
}
@Test mutantTest29: run mutantVal29 for 3 expect 1

val mutantVal30 {
	some disj Element0: Element {some disj Array0: Array {
		Element = Element0
		Array = Array0
		i2e = Array0->1->Element0 + Array0->2->Element0
		length = Array0->3
	}}
}
@Test mutantTest30: run mutantVal30 for 3 expect 1

val mutantVal31 {
	some disj Element0: Element {some disj Array0: Array {
		Element = Element0
		Array = Array0
		i2e = Array0->1->Element0 + Array0->2->Element0 + Array0->4->Element0 + Array0->5->Element0
		length = Array0->3
	}}
}
@Test mutantTest31: run mutantVal31 for 3 expect 0

val mutantVal32 {
	some disj Element0: Element {some disj Array0: Array {
		Element = Element0
		Array = Array0
		i2e = Array0->1->Element0 + Array0->5->Element0 + Array0->7->Element0
		length = Array0->7
	}}
}
@Test mutantTest32: run mutantVal32 for 3 expect 0

val mutantVal33 {
	some disj Element0: Element {some disj Array0: Array {
		Element = Element0
		Array = Array0
		i2e = Array0->1->Element0 + Array0->2->Element0 + Array0->3->Element0 + Array0->4->Element0 + Array0->5->Element0 + Array0->6->Element0
		length = Array0->7
	}}
}
@Test mutantTest33: run mutantVal33 for 3 expect 1

val mutantVal34 {
	some disj Element0: Element {some disj Array0: Array {
		Element = Element0
		Array = Array0
		i2e = Array0->3->Element0 + Array0->4->Element0 + Array0->5->Element0 + Array0->6->Element0
		length = Array0->3
	}}
}
@Test mutantTest34: run mutantVal34 for 3 expect 0

val mutantVal35 {
	some disj Element0: Element {some disj Array0: Array {
		Element = Element0
		Array = Array0
		i2e = Array0->1->Element0 + Array0->2->Element0 + Array0->3->Element0 + Array0->4->Element0 + Array0->5->Element0
		length = Array0->6
	}}
}
@Test mutantTest35: run mutantVal35 for 3 expect 1

val mutantVal36 {
	some disj Element0: Element {some disj Array0: Array {
		Element = Element0
		Array = Array0
		i2e = Array0->1->Element0 + Array0->3->Element0
		length = Array0->3
	}}
}
@Test mutantTest36: run mutantVal36 for 3 expect 0

val mutantVal37 {
	some disj Element0: Element {some disj Array0: Array {
		Element = Element0
		Array = Array0
		i2e = Array0->2->Element0
		length = Array0->3
	}}
}
@Test mutantTest37: run mutantVal37 for 3 expect 1
