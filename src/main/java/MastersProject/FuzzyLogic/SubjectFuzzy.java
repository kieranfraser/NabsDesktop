package MastersProject.FuzzyLogic;

import com.fuzzylite.Engine;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.norm.s.Maximum;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Ramp;
import com.fuzzylite.term.Triangle;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;

public class SubjectFuzzy {
	Engine engine;
	InputVariable subjectImportance;
	InputVariable eventRelevance;
	OutputVariable subjectRelevance;
	
	public SubjectFuzzy(){
	    engine = new Engine();
	    engine.setName("subject-context");
	
		subjectImportance = new InputVariable();
		subjectImportance.setName("subjectImportance");
		subjectImportance.setRange(0.000, 1.000);
		subjectImportance.addTerm(new Triangle("NIP", 0.000, 0.000, 0.400));
		subjectImportance.addTerm(new Triangle("IMPORTANT", 0.200, 0.500, 0.800));
		subjectImportance.addTerm(new Triangle("VIP", 0.600, 1.000, 1.000));
		engine.addInputVariable(subjectImportance);
		
		eventRelevance = new InputVariable();
		eventRelevance.setName("EventRelevance");
		eventRelevance.setRange(0.000, 1.000);
		eventRelevance.addTerm(new Triangle("NOTRELEVANT", 0.000, 0.350, 0.700));
		eventRelevance.addTerm(new Triangle("RELEVANT", 0.350, 0.700, 1.000));
		engine.addInputVariable(eventRelevance);
		
		subjectRelevance = new OutputVariable();
		subjectRelevance.setEnabled(true);
		subjectRelevance.setName("subjectRelevance");
		subjectRelevance.setRange(0.000, 1.000);
		subjectRelevance.fuzzyOutput().setAccumulation(new Maximum());
		subjectRelevance.setDefuzzifier(new Centroid(200));
		subjectRelevance.setDefaultValue(Double.NaN);
		subjectRelevance.setLockPreviousOutputValue(false);
		subjectRelevance.setLockOutputValueInRange(false);
		subjectRelevance.addTerm(new Triangle("LOW", 0.000, 0.250, 0.500));
		subjectRelevance.addTerm(new Triangle("MEDIUM", 0.250, 0.500, 0.750));
		subjectRelevance.addTerm(new Triangle("HIGH", 0.500, 0.750, 1.000));
		engine.addOutputVariable(subjectRelevance);
		
		RuleBlock ruleBlock = new RuleBlock();
		ruleBlock.addRule(Rule.parse("if subjectImportance is NIP and EventRelevance is NOTRELEVANT then subjectRelevance is LOW", engine));
		ruleBlock.addRule(Rule.parse("if subjectImportance is IMPORTANT and EventRelevance is NOTRELEVANT then subjectRelevance is MEDIUM", engine));
		ruleBlock.addRule(Rule.parse("if subjectImportance is VIP and EventRelevance is NOTRELEVANT then subjectRelevance is HIGH", engine));
		ruleBlock.addRule(Rule.parse("if subjectImportance is NIP and EventRelevance is RELEVANT then subjectRelevance is MEDIUM", engine));
		ruleBlock.addRule(Rule.parse("if subjectImportance is IMPORTANT and EventRelevance is RELEVANT then subjectRelevance is HIGH", engine));
		ruleBlock.addRule(Rule.parse("if subjectImportance is VIP and EventRelevance is RELEVANT then subjectRelevance is HIGH", engine));
		engine.addRuleBlock(ruleBlock);
		

		engine.configure("Minimum", "", "Minimum", "Maximum", "Centroid");

	}
	
	public double processSubject(double subjectInput, double eventInput){
		subjectImportance.setInputValue(subjectInput);
		eventRelevance.setInputValue(eventInput);
		engine.process();
		return subjectRelevance.getOutputValue();		
	}
	
}
