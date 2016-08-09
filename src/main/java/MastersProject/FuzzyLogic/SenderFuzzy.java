package MastersProject.FuzzyLogic;

import com.fuzzylite.Engine;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.norm.s.Maximum;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Ramp;
import com.fuzzylite.term.Rectangle;
import com.fuzzylite.term.SShape;
import com.fuzzylite.term.Trapezoid;
import com.fuzzylite.term.Triangle;
import com.fuzzylite.term.ZShape;
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
		senderImportance.addTerm(new Triangle("NIP", 0.000, 0.000, 0.5));
		senderImportance.addTerm(new Trapezoid("IMPORTANT", 0.18, 0.5, 0.65, 0.86));
		senderImportance.addTerm(new Triangle("VIP", 0.600, 1.000, 1.000));
		engine.addInputVariable(senderImportance);
		
		eventRelevance = new InputVariable();
		eventRelevance.setName("EventRelevance");
		eventRelevance.setRange(0.000, 1.001);
		eventRelevance.addTerm(new ZShape("NOTRELEVANT", 0.1, 0.6));
		eventRelevance.addTerm(new Trapezoid("RELEVANT", 0.18, 0.5, 0.65, 0.86));
		eventRelevance.addTerm(new SShape("VERYRELEVANT", 0.7, 1));
		engine.addInputVariable(eventRelevance);
		
		senderRelevance = new OutputVariable();
		senderRelevance.setEnabled(true);
		senderRelevance.setName("SenderRelevance");
		senderRelevance.setRange(0.000, 1.000);
		senderRelevance.fuzzyOutput().setAccumulation(new Maximum()); 
		senderRelevance.setDefuzzifier(new Centroid(100));
		senderRelevance.setDefaultValue(Double.NaN);
		senderRelevance.setLockPreviousOutputValue(false);
		senderRelevance.setLockOutputValueInRange(false);
		senderRelevance.addTerm(new Trapezoid("LOW", 0.000, 0.000, 0.1, 0.4));
		senderRelevance.addTerm(new Trapezoid("MEDIUM", 0.12, 0.28, 0.45, 1.4));
		senderRelevance.addTerm(new Triangle("HIGH", 0.9, 1.000, 1.000));
		engine.addOutputVariable(senderRelevance);
		
		RuleBlock ruleBlock = new RuleBlock();
		ruleBlock.addRule(Rule.parse("if SenderImportance is NIP and EventRelevance is NOTRELEVANT then SenderRelevance is LOW", engine));
		ruleBlock.addRule(Rule.parse("if SenderImportance is IMPORTANT and EventRelevance is NOTRELEVANT then SenderRelevance is MEDIUM", engine));
		ruleBlock.addRule(Rule.parse("if SenderImportance is VIP and EventRelevance is NOTRELEVANT then SenderRelevance is MEDIUM", engine));
		ruleBlock.addRule(Rule.parse("if SenderImportance is NIP and EventRelevance is RELEVANT then SenderRelevance is LOW", engine));
		ruleBlock.addRule(Rule.parse("if SenderImportance is IMPORTANT and EventRelevance is RELEVANT then SenderRelevance is MEDIUM", engine));
		ruleBlock.addRule(Rule.parse("if SenderImportance is VIP and EventRelevance is RELEVANT then SenderRelevance is HIGH", engine));
		ruleBlock.addRule(Rule.parse("if SenderImportance is NIP and EventRelevance is VERYRELEVANT then SenderRelevance is MEDIUM", engine));
		ruleBlock.addRule(Rule.parse("if SenderImportance is IMPORTANT and EventRelevance is VERYRELEVANT then SenderRelevance is HIGH", engine));
		ruleBlock.addRule(Rule.parse("if SenderImportance is VIP and EventRelevance is VERYRELEVANT then SenderRelevance is HIGH", engine));
		engine.addRuleBlock(ruleBlock);
		
									// conjunction, disjunction, implication, accumulation, defuzzifier
		engine.configure("Minimum", "", "Minimum", "Maximum", "Centroid");

	}
	
	public double processSender(double senderInput, double eventInput){
		senderImportance.setInputValue(senderInput);
		eventRelevance.setInputValue(eventInput);
		engine.process();
		return senderRelevance.getOutputValue();		
	}
	
}
