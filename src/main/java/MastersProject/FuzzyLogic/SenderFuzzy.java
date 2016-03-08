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

public class SenderFuzzy {
	Engine engine;
	InputVariable senderImportance;
	InputVariable eventRelevance;
	OutputVariable senderRelevance;
	
	public SenderFuzzy(){
	    engine = new Engine();
	    engine.setName("sender-context");
	
		senderImportance = new InputVariable();
		senderImportance.setName("SenderImportance");
		senderImportance.setRange(0.000, 1.000);
		senderImportance.addTerm(new Ramp("NIP", 0.000, 0.400));
		senderImportance.addTerm(new Triangle("IMPORTANT", 0.200, 0.500, 0.800));
		senderImportance.addTerm(new Ramp("VIP", 0.600, 1.000));
		engine.addInputVariable(senderImportance);
		
		eventRelevance = new InputVariable();
		eventRelevance.setName("EventRelevance");
		eventRelevance.setRange(0.000, 1.000);
		eventRelevance.addTerm(new Triangle("NOTRELEVANT", 0.000, 0.350, 0.700));
		eventRelevance.addTerm(new Triangle("RELEVANT", 0.350, 0.700, 1.000));
		engine.addInputVariable(eventRelevance);
		
		senderRelevance = new OutputVariable();
		senderRelevance.setEnabled(true);
		senderRelevance.setName("SenderRelevance");
		senderRelevance.setRange(0.000, 1.000);
		senderRelevance.fuzzyOutput().setAccumulation(new Maximum());
		senderRelevance.setDefuzzifier(new Centroid(200));
		senderRelevance.setDefaultValue(Double.NaN);
		senderRelevance.setLockPreviousOutputValue(false);
		senderRelevance.setLockOutputValueInRange(false);
		senderRelevance.addTerm(new Triangle("LOW", 0.000, 0.250, 0.500));
		senderRelevance.addTerm(new Triangle("MEDIUM", 0.250, 0.500, 0.750));
		senderRelevance.addTerm(new Triangle("HIGH", 0.500, 0.750, 1.000));
		engine.addOutputVariable(senderRelevance);
		
		RuleBlock ruleBlock = new RuleBlock();
		ruleBlock.addRule(Rule.parse("if SenderImportance is NIP and EventRelevance is NOTRELEVANT then SenderRelevance is LOW", engine));
		ruleBlock.addRule(Rule.parse("if SenderImportance is IMPORTANT and EventRelevance is NOTRELEVANT then SenderRelevance is MEDIUM", engine));
		ruleBlock.addRule(Rule.parse("if SenderImportance is VIP and EventRelevance is NOTRELEVANT then SenderRelevance is HIGH", engine));
		ruleBlock.addRule(Rule.parse("if SenderImportance is NIP and EventRelevance is RELEVANT then SenderRelevance is MEDIUM", engine));
		ruleBlock.addRule(Rule.parse("if SenderImportance is IMPORTANT and EventRelevance is RELEVANT then SenderRelevance is HIGH", engine));
		ruleBlock.addRule(Rule.parse("if SenderImportance is VIP and EventRelevance is RELEVANT then SenderRelevance is HIGH", engine));
		engine.addRuleBlock(ruleBlock);
		

		engine.configure("Minimum", "", "Minimum", "Maximum", "Centroid");

	}
	
	public double processSender(double senderInput, double eventInput){
		senderImportance.setInputValue(senderInput);
		eventRelevance.setInputValue(eventInput);
		engine.process();
		return senderRelevance.getOutputValue();		
	}
	
}
