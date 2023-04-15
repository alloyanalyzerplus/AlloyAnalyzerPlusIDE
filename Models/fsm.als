one sig FSM {
  start: set State,
  stop: set State
}

sig State {
  transition: set State
}

// Part (a)
fact OneStartAndStop {
  // FSM only has one start state.
  #FSM.start = 1
  // FSM only has one stop state.
  #FSM.stop = 1
}

// Part (b)
fact ValidStartAndStop {
  // A state cannot be both a start state and a stop state.
  FSM.start != FSM.stop
  // No transition ends at the start state.
  // Fix: replace "!=" with "!in".  If we consider all facts together, then this fix is sufficient.
  all s:State | FSM.start != s.transition
  // No transition begins at the stop state.
  no FSM.stop.transition
}

// Part (c)
fact Reachability {
  // All states are reachable from the start state.
  State = FSM.start.*transition
  // The stop state is reachable from any state.
  all s:(State - FSM.stop) | FSM.stop in s.*transition
}

val test0 {
some disj FSM0: FSM {some disj State0, State1: State {
FSM = FSM0
start = FSM0->State1
stop = FSM0->State0
State = State0 + State1
transition = State1->State0
}}
}
@Test test0: run test0 for 3 expect 1

val mutantVal0 {
	some disj FSM0, FSM1, FSM2: FSM {some disj State0, State1: State {
		FSM = FSM0 + FSM1 + FSM2
		start = FSM2->State1
		stop = FSM1->State0
		State = State0 + State1
		transition = State1->State0
	}}
}
@Test mutantTest0: run mutantVal0 for 3 expect 0

val mutantVal1 {
	some disj FSM0, FSM1: FSM {some disj State0, State1: State {
		FSM = FSM0 + FSM1
		start = FSM1->State1
		stop = FSM1->State0
		State = State0 + State1
		transition = State1->State0 + State1->State1
	}}
}
@Test mutantTest1: run mutantVal1 for 3 expect 0

val mutantVal2 {
	some disj FSM0: FSM {some disj State0, State1: State {
		FSM = FSM0
		start = FSM0->State1
		stop = FSM0->State0
		State = State0 + State1
		transition = State1->State0 + State1->State1
	}}
}
@Test mutantTest2: run mutantVal2 for 3 expect 0

val mutantVal3 {
	some disj FSM0: FSM {some disj State0, State1: State {
		FSM = FSM0
		start = FSM0->State1
		stop = FSM0->State0
		State = State0 + State1
		transition = State1->State0
	}}
}
@Test mutantTest3: run mutantVal3 for 3 expect 1

val mutantVal4 {
	some disj FSM0: FSM {some disj State0, State1: State {
		FSM = FSM0
		start = FSM0->State0
		stop = FSM0->State1
		State = State0 + State1
		transition = State0->State0 + State0->State1
	}}
}
@Test mutantTest4: run mutantVal4 for 3 expect 0

val mutantVal5 {
	some disj FSM0: FSM {some disj State0, State1: State {
		FSM = FSM0
		start = FSM0->State0 + FSM0->State1
		no stop
		State = State0 + State1
		no transition
	}}
}
@Test mutantTest5: run mutantVal5 for 3 expect 0

val mutantVal6 {
	some disj FSM0: FSM {some disj State0, State1: State {
		FSM = FSM0
		start = FSM0->State1
		no stop
		State = State0 + State1
		transition = State1->State0
	}}
}
@Test mutantTest6: run mutantVal6 for 3 expect 0

val mutantVal7 {
	some disj FSM0: FSM {some disj State0, State1, State2: State {
		FSM = FSM0
		start = FSM0->State1 + FSM0->State2
		stop = FSM0->State0
		State = State0 + State1 + State2
		transition = State1->State2 + State2->State0
	}}
}
@Test mutantTest7: run mutantVal7 for 3 expect 0

val mutantVal8 {
	some disj FSM0: FSM {some disj State0, State1, State2: State {
		FSM = FSM0
		start = FSM0->State2
		stop = FSM0->State0 + FSM0->State1
		State = State0 + State1 + State2
		transition = State2->State0 + State2->State1
	}}
}
@Test mutantTest8: run mutantVal8 for 3 expect 0

val mutantVal9 {
	some disj FSM0: FSM {some disj State0, State1: State {
		FSM = FSM0
		start = FSM0->State1
		stop = FSM0->State1
		State = State0 + State1
		transition = State0->State1 + State1->State0 + State1->State1
	}}
}
@Test mutantTest9: run mutantVal9 for 3 expect 0

val mutantVal10 {
	some disj FSM0: FSM {some disj State0, State1: State {
		FSM = FSM0
		start = FSM0->State1
		stop = FSM0->State0
		State = State0 + State1
		transition = State0->State1 + State1->State0 + State1->State1
	}}
}
@Test mutantTest10: run mutantVal10 for 3 expect 0

val mutantVal11 {
	some disj FSM0: FSM {some disj State0: State {
		FSM = FSM0
		start = FSM0->State0
		stop = FSM0->State0
		State = State0
		no transition
	}}
}
@Test mutantTest11: run mutantVal11 for 3 expect 0

val mutantVal12 {
	some disj FSM0: FSM {some disj State0, State1, State2: State {
		FSM = FSM0
		start = FSM0->State2
		stop = FSM0->State1
		State = State0 + State1 + State2
		transition = State0->State2 + State2->State0 + State2->State1
	}}
}
@Test mutantTest12: run mutantVal12 for 3 expect 0

val mutantVal13 {
	some disj FSM0: FSM {some disj State0, State1: State {
		FSM = FSM0
		start = FSM0->State1
		stop = FSM0->State0
		State = State0 + State1
		transition = State0->State0 + State0->State1 + State1->State0 + State1->State1
	}}
}
@Test mutantTest13: run mutantVal13 for 3 expect 0

val mutantVal14 {
	some disj FSM0: FSM {some disj State0, State1, State2: State {
		FSM = FSM0
		start = FSM0->State2
		stop = FSM0->State1
		State = State0 + State1 + State2
		transition = State0->State2 + State2->State0 + State2->State1 + State2->State2
	}}
}
@Test mutantTest14: run mutantVal14 for 3 expect 0

val mutantVal15 {
	some disj FSM0: FSM {some disj State0, State1: State {
		FSM = FSM0
		start = FSM0->State1
		stop = FSM0->State0
		State = State0 + State1
		transition = State0->State0 + State1->State0
	}}
}
@Test mutantTest15: run mutantVal15 for 3 expect 0

val mutantVal16 {
	some disj FSM0: FSM {some disj State0, State1: State {
		FSM = FSM0
		start = FSM0->State1
		stop = FSM0->State0
		State = State0 + State1
		no transition
	}}
}
@Test mutantTest16: run mutantVal16 for 3 expect 0

val mutantVal17 {
	some disj FSM0: FSM {some disj State0, State1, State2: State {
		FSM = FSM0
		start = FSM0->State2
		stop = FSM0->State1
		State = State0 + State1 + State2
		transition = State0->State1 + State2->State1
	}}
}
@Test mutantTest17: run mutantVal17 for 3 expect 0

val mutantVal18 {
	some disj FSM0: FSM {some disj State0, State1, State2: State {
		FSM = FSM0
		start = FSM0->State2
		stop = FSM0->State1
		State = State0 + State1 + State2
		transition = State0->State0 + State2->State0 + State2->State1
	}}
}
@Test mutantTest18: run mutantVal18 for 3 expect 0

val mutantVal19 {
	some disj FSM0: FSM {some disj State0, State1, State2: State {
		FSM = FSM0
		start = FSM0->State2
		stop = FSM0->State1
		State = State0 + State1 + State2
		transition = State0->State1 + State2->State0
	}}
}
@Test mutantTest19: run mutantVal19 for 3 expect 1
