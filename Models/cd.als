sig Class {
  ext: lone Class
}

one sig Object extends Class {}

pred AllExtObject() {
  // Each class other than Object is a sub-class of Object.
  // The body of the formula is wrong.
  // Fix: replace "c.*ext" with "Object.^~ext" or replace "c in c.*ext" with "Object in c.*ext".
 all c: Class - Object | c in c.*ext
}

val Test0 {
some disj Object0: Object {some disj Object0, Class0, Class1: Class {
Object = Object0
Class = Object0 + Class0 + Class1
ext = Class1->Object0 
@cmd:{AllExtObject[]}
}}
}
@Test Test0: run Test0 for 3 expect 0

val mutantVal0 {
	some disj Object0: Object {some disj Object0, Class0: Class {
		Object = Object0
		Class = Object0 + Class0
		ext = Object0->Class0 + Class0->Class0
	}}
}
@Test mutantTest0: run mutantVal0 for 3 expect 1

val mutantVal1 {
	some disj Object0: Object {some disj Object0, Class0: Class {
		Object = Object0
		Class = Object0 + Class0
		ext = Object0->Class0 + Class0->Object0 + Class0->Class0
	}}
}
@Test mutantTest1: run mutantVal1 for 3 expect 0

val mutantVal2 {
	some disj Object0: Object {some disj Object0, Class0: Class {
		Object = Object0
		Class = Object0 + Class0
		ext = Class0->Class0
	}}
}
@Test mutantTest2: run mutantVal2 for 3 expect 1

val mutantVal3 {
	some disj Object0: Object {some disj Object0, Class0: Class {
		Object = Object0
		Class = Object0 + Class0
		ext = Object0->Object0 + Object0->Class0 + Class0->Object0 + Class0->Class0
	}}
}
@Test mutantTest3: run mutantVal3 for 3 expect 0

val mutantVal4 {
	some disj Class0, Class1, Class2: Class {
		no Object
		Class = Class0 + Class1 + Class2
		ext = Class0->Class2 + Class1->Class0
	}
}
@Test mutantTest4: run mutantVal4 for 3 expect 0

val mutantVal5 {
	some disj Object0, Object1: Object {some disj Class0, Object0, Object1: Class {
		Object = Object0 + Object1
		Class = Class0 + Object0 + Object1
		ext = Class0->Object1 + Object0->Object0 + Object1->Class0
	}}
}
@Test mutantTest5: run mutantVal5 for 3 expect 0

val mutantVal6 {
	some disj Object0, Object1: Object {some disj Class0, Object0, Object1: Class {
		Object = Object0 + Object1
		Class = Class0 + Object0 + Object1
		ext = Class0->Object1 + Object0->Class0
	}}
}
@Test mutantTest6: run mutantVal6 for 3 expect 0

val mutantVal7 {
	some disj Object0: Object {some disj Object0, Class0: Class {
		Object = Object0
		Class = Object0 + Class0
		ext = Object0->Class0 + Class0->Class0
		@cmd:{ AllExtObject[] }
	}}
}
@Test mutantTest7: run mutantVal7 for 3 expect 0

val mutantVal8 {
	some disj Object0: Object {some disj Object0, Class0, Class1: Class {
		Object = Object0
		Class = Object0 + Class0 + Class1
		ext = Class0->Class0 + Class1->Object0
		@cmd:{ AllExtObject[] }
	}}
}
@Test mutantTest8: run mutantVal8 for 3 expect 0

val mutantVal9 {
	some disj Object0: Object {some disj Object0: Class {
		Object = Object0
		Class = Object0
		no ext
		@cmd:{ AllExtObject[] }
	}}
}
@Test mutantTest9: run mutantVal9 for 3 expect 1

val mutantVal10 {
	some disj Object0: Object {some disj Object0, Class0: Class {
		Object = Object0
		Class = Object0 + Class0
		ext = Object0->Object0 + Class0->Object0
		@cmd:{ AllExtObject[] }
	}}
}
@Test mutantTest10: run mutantVal10 for 3 expect 1

val mutantVal11 {
	some disj Object0: Object {some disj Object0, Class0: Class {
		Object = Object0
		Class = Object0 + Class0
		ext = Class0->Object0
		@cmd:{ AllExtObject[] }
	}}
}
@Test mutantTest11: run mutantVal11 for 3 expect 1

val mutantVal12 {
	some disj Object0: Object {some disj Object0, Class0: Class {
		Object = Object0
		Class = Object0 + Class0
		ext = Object0->Class0 + Class0->Object0
		@cmd:{ AllExtObject[] }
	}}
}
@Test mutantTest12: run mutantVal12 for 3 expect 1
