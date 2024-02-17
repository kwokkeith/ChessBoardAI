

@FunctionalInterface // Forces only one (abstract) method inside the interface
interface Evaluation_Function {
	int evaluate(State current_state);
}