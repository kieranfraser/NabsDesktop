package MastersProject.FuzzyLogic;

import com.fuzzylite.Engine;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.norm.s.Maximum;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Ramp;
import com.fuzzylite.term.SShape;
import com.fuzzylite.term.Term;
import com.fuzzylite.term.Trapezoid;
import com.fuzzylite.term.Triangle;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;

import PhDProject.Managers.ParameterManager;

public class AlertFuzzy {
	Engine engine;
	InputVariable senderInput;
	InputVariable subjectInput;
	InputVariable appInput;
	OutputVariable alertOutput;

	String[] params;
	
	public AlertFuzzy(){

		params = ParameterManager.getParamManager().getAlertParams();
		
	    engine = new Engine();
	    engine.setName("alert-context");
	
	    senderInput = new InputVariable();
	    senderInput.setName("SenderInput");
	    senderInput.setRange(0.000, 1.000);
	    senderInput.addTerm(new Triangle("NIP", 0.000, 0.000, 0.400));
	    senderInput.addTerm(new Triangle("IMPORTANT", 0.200, 0.500, 0.800));
	    senderInput.addTerm(new Triangle("VIP", 0.600, 1.000, 1.000));
		engine.addInputVariable(senderInput);
		
		subjectInput = new InputVariable();
		subjectInput.setName("SubjectInput");
		subjectInput.setRange(0.000, 1.000);
		subjectInput.addTerm(new Triangle("NIP", 0.000, 0.000, 0.400));
		subjectInput.addTerm(new Triangle("IMPORTANT", 0.200, 0.500, 0.800));
		subjectInput.addTerm(new Triangle("VIP", 0.600, 1.000, 1.000));
		engine.addInputVariable(subjectInput);
		
		appInput = new InputVariable();
		appInput.setName("AppInput");
		appInput.setRange(0.000, 1.000);
		appInput.addTerm(new Triangle("NIP", 0.000, 0.000, 0.500));
		appInput.addTerm(new Trapezoid("IMPORTANT", 0.2, 0.55, 0.63, 0.87));
		appInput.addTerm(new Triangle("VIP", 0.600, 1.000, 1.000));
		engine.addInputVariable(appInput);
		
		alertOutput = new OutputVariable();
		alertOutput.setEnabled(true);
		alertOutput.setName("AlertOutput");
		alertOutput.setRange(0.000, 100.001);
		alertOutput.fuzzyOutput().setAccumulation(new Maximum());
		alertOutput.setDefuzzifier(new Centroid(200));
		alertOutput.setDefaultValue(Double.NaN);
		alertOutput.setLockPreviousOutputValue(false);
		alertOutput.setLockOutputValueInRange(false);
		alertOutput.addTerm(new Triangle("NOW", 0.000, 0.000, 10.01));
		alertOutput.addTerm(new Triangle("VERYSOON", 5.000, 10.001, 20.001));
		alertOutput.addTerm(new Triangle("SOON", 15.000, 20.000, 40.001));
		alertOutput.addTerm(new Triangle("LATER", 25.000, 50.000, 70.001));
		alertOutput.addTerm(new SShape("MUCHLATER", 57.5, 77.5));
		engine.addOutputVariable(alertOutput);
		
		RuleBlock ruleBlock = new RuleBlock();
		ruleBlock.addRule(Rule.parse("if SenderInput is NIP and SubjectInput is NIP and AppInput is NIP then AlertOutput is "+params[0], engine));
		ruleBlock.addRule(Rule.parse("if SenderInput is NIP and SubjectInput is NIP and AppInput is IMPORTANT then AlertOutput is "+params[1], engine));
		ruleBlock.addRule(Rule.parse("if SenderInput is NIP and SubjectInput is NIP and AppInput is VIP then AlertOutput is "+params[2], engine));
		
		ruleBlock.addRule(Rule.parse("if SenderInput is NIP and SubjectInput is IMPORTANT and AppInput is NIP then AlertOutput is "+params[3], engine));
		ruleBlock.addRule(Rule.parse("if SenderInput is NIP and SubjectInput is IMPORTANT and AppInput is IMPORTANT then AlertOutput is "+params[4], engine));
		ruleBlock.addRule(Rule.parse("if SenderInput is NIP and SubjectInput is IMPORTANT and AppInput is VIP then AlertOutput is "+params[5], engine));
		
		ruleBlock.addRule(Rule.parse("if SenderInput is NIP and SubjectInput is VIP and AppInput is NIP then AlertOutput is "+params[6], engine));
		ruleBlock.addRule(Rule.parse("if SenderInput is NIP and SubjectInput is VIP and AppInput is IMPORTANT then AlertOutput is "+params[7], engine));
		ruleBlock.addRule(Rule.parse("if SenderInput is NIP and SubjectInput is VIP and AppInput is VIP then AlertOutput is "+params[8], engine));
		
		ruleBlock.addRule(Rule.parse("if SenderInput is IMPORTANT and SubjectInput is NIP and AppInput is NIP then AlertOutput is "+params[9], engine));
		ruleBlock.addRule(Rule.parse("if SenderInput is IMPORTANT and SubjectInput is NIP and AppInput is IMPORTANT then AlertOutput is "+params[10], engine));
		ruleBlock.addRule(Rule.parse("if SenderInput is IMPORTANT and SubjectInput is NIP and AppInput is VIP then AlertOutput is "+params[11], engine));
		
		ruleBlock.addRule(Rule.parse("if SenderInput is IMPORTANT and SubjectInput is IMPORTANT and AppInput is NIP then AlertOutput is "+params[12], engine));
		ruleBlock.addRule(Rule.parse("if SenderInput is IMPORTANT and SubjectInput is IMPORTANT and AppInput is IMPORTANT then AlertOutput is "+params[13], engine));
		ruleBlock.addRule(Rule.parse("if SenderInput is IMPORTANT and SubjectInput is IMPORTANT and AppInput is VIP then AlertOutput is "+params[14], engine));
		
		ruleBlock.addRule(Rule.parse("if SenderInput is IMPORTANT and SubjectInput is VIP and AppInput is NIP then AlertOutput is "+params[15], engine));
		ruleBlock.addRule(Rule.parse("if SenderInput is IMPORTANT and SubjectInput is VIP and AppInput is IMPORTANT then AlertOutput is "+params[16], engine));
		ruleBlock.addRule(Rule.parse("if SenderInput is IMPORTANT and SubjectInput is VIP and AppInput is VIP then AlertOutput is "+params[17], engine));

		ruleBlock.addRule(Rule.parse("if SenderInput is VIP and SubjectInput is NIP and AppInput is NIP then AlertOutput is "+params[18], engine));
		ruleBlock.addRule(Rule.parse("if SenderInput is VIP and SubjectInput is NIP and AppInput is IMPORTANT then AlertOutput is "+params[19], engine));
		ruleBlock.addRule(Rule.parse("if SenderInput is VIP and SubjectInput is NIP and AppInput is VIP then AlertOutput is "+params[20], engine));
		
		ruleBlock.addRule(Rule.parse("if SenderInput is VIP and SubjectInput is IMPORTANT and AppInput is NIP then AlertOutput is "+params[21], engine));
		ruleBlock.addRule(Rule.parse("if SenderInput is VIP and SubjectInput is IMPORTANT and AppInput is IMPORTANT then AlertOutput is "+params[22], engine));
		ruleBlock.addRule(Rule.parse("if SenderInput is VIP and SubjectInput is IMPORTANT and AppInput is VIP then AlertOutput is "+params[23], engine));
		
		ruleBlock.addRule(Rule.parse("if SenderInput is VIP and SubjectInput is VIP and AppInput is NIP then AlertOutput is "+params[24], engine));
		ruleBlock.addRule(Rule.parse("if SenderInput is VIP and SubjectInput is VIP and AppInput is IMPORTANT then AlertOutput is "+params[25], engine));
		ruleBlock.addRule(Rule.parse("if SenderInput is VIP and SubjectInput is VIP and AppInput is VIP then AlertOutput is "+params[26], engine));
		
		engine.addRuleBlock(ruleBlock);
		

		engine.configure("Minimum", "", "Minimum", "Maximum", "Centroid");
	}
	
	public double processalert(double sender, double subject, double app){
		senderInput.setInputValue(sender);
		subjectInput.setInputValue(subject);
		appInput.setInputValue(app);
		engine.process();
		return alertOutput.getOutputValue();		
	}
	
}
