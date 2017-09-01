package Constraints;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import moa.classifiers.trees.HoeffdingTree;
import LossyCounting.LossyCounting;
import LossyCounting.LCTemplateReplayer;
import Utils.ComputeKPI;
//import prompt.onlinedeclare.utils.DeclareModel;
import Utils.Pair;
import Utils.Utils;

import com.yahoo.labs.samoa.instances.*;

import moa.classifiers.trees.HoeffdingTree;
import moa.core.*;

public class ChainResponse implements LCTemplateReplayer {

	private HashMap<String, Instances> instanceForTree = new HashMap<String, Instances>();
	private HashMap<String, Object> attribute;
	private HashMap<String, ArrayList<String>> nominal = new HashMap<String, ArrayList<String>>();
	ArrayList<Attribute> myAttr = new ArrayList<Attribute>(20);
	ArrayList<Attribute> myAttrTr ;
	private HashMap<String, Integer> attIndex = new HashMap<String, Integer>();
	
	boolean first = true, fulf = true;
	private Model modello = new Model();
	private HashMap<String, HashMap<String, Pair<Integer, HoeffdingTree>>> mc = new HashMap<String, HashMap<String, Pair<Integer, HoeffdingTree>>>();
	private LossyModel mod = new LossyModel();
	int nr = 0, en=0, cc=5;
	
	private HashSet<String> activityLabelsChResponse = new HashSet<String>();
	private LossyCounting<HashMap<String, Integer>> activityLabelsCounterChResponse = new LossyCounting<HashMap<String, Integer>>();
	private LossyCounting<HashMap<String, HashMap<String, Integer>>> fulfilledConstraintsPerTraceCh = new LossyCounting<HashMap<String, HashMap<String, Integer>>>();
	private LossyCounting<HashMap<String, HashMap<String, Integer>>> violatedConstraintsPerTraceCh = new LossyCounting<HashMap<String, HashMap<String, Integer>>>();
	private LossyCounting<String> lastActivity = new LossyCounting<String>();

	File file = new File("/home/matte/Scrivania/Out/OutChResponse.txt");
	FileWriter fw = null;
	BufferedWriter brf;
	PrintWriter printout;{			
	try {
		fw = new FileWriter(file);
	} catch (IOException e2) {
		e2.printStackTrace();
	}
	brf = new BufferedWriter(fw);
	printout = new PrintWriter(brf);}
	
	@Override
	public void addObservation(String caseId, Integer currentBucket) {
		HashMap<String, HashMap<String, Integer>> ex1 = new HashMap<String, HashMap<String, Integer>>();
		HashMap<String, Integer> ex2 = new HashMap<String, Integer>();
		@SuppressWarnings("rawtypes")
		Class class1 = ex1.getClass();
		@SuppressWarnings("rawtypes")
		Class class2 = ex2.getClass();
		
		try {
			fulfilledConstraintsPerTraceCh.addObservation(caseId, currentBucket, class1);
			violatedConstraintsPerTraceCh.addObservation(caseId, currentBucket, class1);
			activityLabelsCounterChResponse.addObservation(caseId, currentBucket, class2);
			lastActivity.addObservation(caseId, currentBucket, "".getClass());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cleanup(Integer currentBucket) {
		fulfilledConstraintsPerTraceCh.cleanup(currentBucket);
		violatedConstraintsPerTraceCh.cleanup(currentBucket);
		activityLabelsCounterChResponse.cleanup(currentBucket);
		lastActivity.cleanup(currentBucket);
	}

	@Override
	public void process(XEvent eve, XTrace tr, HashMap<String, ArrayList<String>> nomin, Integer bucketWidth) {
//		Model modello = model;
		int currentBucket , pp=0;
		long start = System.currentTimeMillis();
		long start1, start2, start3, start4, start5, stop1, stop2, stop3, stop4, stop5, time=0;
		bucketWidth=(int)(1000);
		//en++;
		// Collection of attribute of new event

		attribute = new HashMap<String, Object>();
		myAttrTr = new ArrayList<Attribute>(100);
		

		ArrayList<String> classe = new ArrayList<String>(2);
		classe.add("FULFILLMENT");
		classe.add("VIOLATION");
		
		if(first){
			myAttr.add(new Attribute("class", classe));
//			myAttr.add(new Attribute("data1"));
//			myAttr.add(new Attribute("data2"));
//			myAttr.add(new Attribute("data3"));
//			myAttr.add(new Attribute("data4"));
//			myAttr.add(new Attribute("data5"));
//			myAttr.add(new Attribute("data6"));
//			myAttr.add(new Attribute("data7"));
//			myAttr.add(new Attribute("data8"));
//			myAttr.add(new Attribute("data9"));
//			myAttr.add(new Attribute("data10"));
//			myAttr.add(new Attribute("data11"));
//			myAttr.add(new Attribute("data12"));
//			myAttr.add(new Attribute("data13"));
//			myAttr.add(new Attribute("data14"));
//			myAttr.add(new Attribute("data15"));
//			myAttr.add(new Attribute("data16"));
//			myAttr.add(new Attribute("data17"));
//			myAttr.add(new Attribute("data18"));
//			myAttr.add(new Attribute("data19"));
//			myAttr.add(new Attribute("data20"));;
//			myAttr.add(new Attribute("org:group", nomin.get("org:group")));
//			myAttr.add(new Attribute("Producer code", nomin.get("Producer code")));
			myAttr.add(new Attribute("Section", nomin.get("Section")));
			myAttr.add(new Attribute("Activity code", nomin.get("Activity code")));
//			myAttr.add(new Attribute("Number of executions"));
			myAttr.add(new Attribute("Specialism code", nomin.get("Specialism code")));
//			myAttr.add(new Attribute("lifecycle:transition", nomin.get("lifecycle:transition")));
//			myAttr.add(new Attribute("time:timestamp", nomin.get("time:timestamp")));
//			myAttr.add(new Attribute("stream:lifecycle:trace-transition", nomin.get("stream:lifecycle:trace-transition")));
//			myAttr.add(new Attribute("concept:name", nomin.get("concept:name")));
			myAttr.add(new Attribute("Age", nomin.get("Age")));
			myAttr.add(new Attribute("Age:1", nomin.get("Age:1")));
			myAttr.add(new Attribute("Age:2", nomin.get("Age:2")));
			myAttr.add(new Attribute("Age:3", nomin.get("Age:3")));
			myAttr.add(new Attribute("Age:4", nomin.get("Age:4")));
			myAttr.add(new Attribute("Age:5", nomin.get("Age:5")));			
			myAttr.add(new Attribute("Diagnosis code", nomin.get("Diagnosis code")));
			myAttr.add(new Attribute("Diagnosis code:1", nomin.get("Diagnosis code:1")));
			myAttr.add(new Attribute("Diagnosis code:2", nomin.get("Diagnosis code:2")));
			myAttr.add(new Attribute("Diagnosis code:3", nomin.get("Diagnosis code:3")));
			myAttr.add(new Attribute("Diagnosis code:4", nomin.get("Diagnosis code:4")));
			myAttr.add(new Attribute("Diagnosis code:5", nomin.get("Diagnosis code:5")));
			myAttr.add(new Attribute("Diagnosis code:6", nomin.get("Diagnosis code:6")));
			myAttr.add(new Attribute("Diagnosis code:7", nomin.get("Diagnosis code:7")));
			myAttr.add(new Attribute("Diagnosis code:8", nomin.get("Diagnosis code:8")));
			myAttr.add(new Attribute("Diagnosis code:9", nomin.get("Diagnosis code:9")));
			myAttr.add(new Attribute("Diagnosis code:10", nomin.get("Diagnosis code:10")));
			myAttr.add(new Attribute("Treatment code", nomin.get("Treatment code")));
			myAttr.add(new Attribute("Treatment code:1", nomin.get("Treatment code:1")));
			myAttr.add(new Attribute("Treatment code:2", nomin.get("Treatment code:2")));
			myAttr.add(new Attribute("Treatment code:3", nomin.get("Treatment code:3")));
			myAttr.add(new Attribute("Treatment code:4", nomin.get("Treatment code:4")));
			myAttr.add(new Attribute("Treatment code:5", nomin.get("Treatment code:5")));
			myAttr.add(new Attribute("Treatment code:6", nomin.get("Treatment code:6")));
			myAttr.add(new Attribute("Treatment code:7", nomin.get("Treatment code:7")));
			myAttr.add(new Attribute("Treatment code:8", nomin.get("Treatment code:8")));
			myAttr.add(new Attribute("Treatment code:9", nomin.get("Treatment code:9")));
			myAttr.add(new Attribute("Treatment code:10", nomin.get("Treatment code:10")));
			myAttr.add(new Attribute("Diagnosis", nomin.get("Diagnosis")));
			myAttr.add(new Attribute("Diagnosis:1", nomin.get("Diagnosis:1")));
			myAttr.add(new Attribute("Diagnosis:2", nomin.get("Diagnosis:2")));
			myAttr.add(new Attribute("Diagnosis:3", nomin.get("Diagnosis:3")));
			myAttr.add(new Attribute("Diagnosis:4", nomin.get("Diagnosis:4")));
			myAttr.add(new Attribute("Diagnosis:5", nomin.get("Diagnosis:5")));
			myAttr.add(new Attribute("Diagnosis:6", nomin.get("Diagnosis:6")));
			myAttr.add(new Attribute("Diagnosis:7", nomin.get("Diagnosis:7")));
			myAttr.add(new Attribute("Diagnosis:8", nomin.get("Diagnosis:8")));
			myAttr.add(new Attribute("Diagnosis:9", nomin.get("Diagnosis:9")));
			myAttr.add(new Attribute("Diagnosis:10", nomin.get("Diagnosis:10")));
			myAttr.add(new Attribute("Diagnosis Treatment Combination ID", nomin.get("Diagnosis Treatment Combination ID")));
			myAttr.add(new Attribute("Diagnosis Treatment Combination ID:1", nomin.get("Diagnosis Treatment Combination ID:1")));
			myAttr.add(new Attribute("Diagnosis Treatment Combination ID:2", nomin.get("Diagnosis Treatment Combination ID:2")));
			myAttr.add(new Attribute("Diagnosis Treatment Combination ID:3", nomin.get("Diagnosis Treatment Combination ID:3")));
			myAttr.add(new Attribute("Diagnosis Treatment Combination ID:4", nomin.get("Diagnosis Treatment Combination ID:4")));
			myAttr.add(new Attribute("Diagnosis Treatment Combination ID:5", nomin.get("Diagnosis Treatment Combination ID:5")));
			myAttr.add(new Attribute("Diagnosis Treatment Combination ID:6", nomin.get("Diagnosis Treatment Combination ID:6")));
			myAttr.add(new Attribute("Diagnosis Treatment Combination ID:7", nomin.get("Diagnosis Treatment Combination ID:7")));
			myAttr.add(new Attribute("Diagnosis Treatment Combination ID:8", nomin.get("Diagnosis Treatment Combination ID:8")));
			myAttr.add(new Attribute("Diagnosis Treatment Combination ID:9", nomin.get("Diagnosis Treatment Combination ID:9")));
			myAttr.add(new Attribute("Diagnosis Treatment Combination ID:10", nomin.get("Diagnosis Treatment Combination ID:10")));
			myAttr.add(new Attribute("Specialism code:1", nomin.get("Specialism code:1")));
			myAttr.add(new Attribute("Specialism code:2", nomin.get("Specialism code:2")));
			myAttr.add(new Attribute("Specialism code:3", nomin.get("Specialism code:3")));
			myAttr.add(new Attribute("Specialism code:4", nomin.get("Specialism code:4")));
			myAttr.add(new Attribute("Specialism code:5", nomin.get("Specialism code:5")));
			myAttr.add(new Attribute("Specialism code:6", nomin.get("Specialism code:6")));
			myAttr.add(new Attribute("Specialism code:7", nomin.get("Specialism code:7")));
			myAttr.add(new Attribute("Specialism code:8", nomin.get("Specialism code:8")));
			myAttr.add(new Attribute("Specialism code:9", nomin.get("Specialism code:9")));
			myAttr.add(new Attribute("Specialism code:10", nomin.get("Specialism code:10")));
//			End date, Age, Diagnosis code, Treatment code, Diagnosis, Diagnosis Treatment Combination ID, Start date
			first=false;
		}	
		
		for(XAttribute attr : eve.getAttributes().values()){
			if(!attribute.containsKey(attr.getKey())){        
				if(isNumeric(attr.toString()) && !attr.getKey().equals("Activity code") && !attr.getKey().equals("Specialism code")){
					double d = Double.parseDouble(attr.toString());
					attribute.put(attr.getKey(), d); 
				}else{
					attribute.put(attr.getKey(), attr.toString());
				}								
			}else if(attribute.containsKey(attr.getKey())){               //!attr.getKey().contains(":") && 
				attribute.remove(attr.getKey());
				attribute.put(attr.getKey(), attr.toString());
			}	
			int l = nomin.get(attr.getKey()).indexOf(attr.toString());
			attIndex.put(attr.getKey(), l);
		}		
		
		for(XAttribute attr : tr.getAttributes().values()){
//			if(!myAttrTr.contains(attr.getKey())){
//				myAttrTr.add(new Attribute(attr.getKey(), nomin.get(attr.getKey())));
//			}		
			int l = nomin.get(attr.getKey()).indexOf(attr.toString());
			attIndex.put(attr.getKey(), l);
		}
		
		for(Attribute attr : myAttr){
			if(!attIndex.containsKey(attr.name()) && !attr.name().equals("class")){
				String attrib = attr.name();
				attIndex.put(attr.name(), nomin.get(attr.name()).indexOf("0"));
			}
		}
		
		String caseId = Utils.getCaseID(tr);
		String event = Utils.getActivityName(eve);
		activityLabelsChResponse.add(event);
		HashMap<String, Integer> counter = new HashMap<String, Integer>();
		if(!activityLabelsCounterChResponse.containsKey(caseId)){
			activityLabelsCounterChResponse.putItem(caseId, counter);
		}else{
			counter = activityLabelsCounterChResponse.getItem(caseId);
		}
		HashMap<String,HashMap<String,Integer>> fulfilledForThisTrace = new HashMap<String,HashMap<String,Integer>>();
		if(!fulfilledConstraintsPerTraceCh.containsKey(caseId)){
			fulfilledConstraintsPerTraceCh.putItem(caseId, fulfilledForThisTrace);
		}else{
			fulfilledForThisTrace = fulfilledConstraintsPerTraceCh.getItem(caseId);
		}
		HashMap<String,HashMap<String,Integer>> violatedForThisTrace = new HashMap<String,HashMap<String,Integer>>();
		if(!violatedConstraintsPerTraceCh.containsKey(caseId)){
			violatedConstraintsPerTraceCh.putItem(caseId, violatedForThisTrace);
		}else{
			violatedForThisTrace = violatedConstraintsPerTraceCh.getItem(caseId);
		}
		String previous = lastActivity.getItem(caseId);
		if(previous!=null && !previous.equals("") && !previous.equals(event)){
			HashMap<String, Integer> secondElementFul = new  HashMap<String, Integer>();
			HashMap<String, Integer> secondElementViol = new  HashMap<String, Integer>();
			if(fulfilledForThisTrace.containsKey(previous)){
				secondElementFul = fulfilledForThisTrace.get(previous);
			}
			if(violatedForThisTrace.containsKey(previous)){
				secondElementViol = violatedForThisTrace.get(previous);
			}
			int nofull = 0;
			if(secondElementFul.containsKey(event)){
				nofull = secondElementFul.get(event);
			}
			//fulfillment
			//HF(previous+"%"+event, createInstance(previous+"%"+event, 0), modello);						
			fulf = true;
			nr++;
			currentBucket = nr/bucketWidth;	
			if(nr>1 && nr<50){
				start1 = System.currentTimeMillis();
				mc = mod.addObservation(previous, event, myAttr, attribute, attIndex, 0, mc); 
				stop1 = System.currentTimeMillis();
				time = time+stop1-start1;
				en++;
				nr=1;	
			}
			//modello.addObservation(, event, currentBucket, bucketWidth, fulf);
			
			secondElementFul.put(event, nofull+1);
			fulfilledForThisTrace.put(previous,secondElementFul);
			fulfilledConstraintsPerTraceCh.putItem(caseId, fulfilledForThisTrace);
			//qualcosa non va nelle regole da trovare le violazioni sono tra event e second?
			for(String second : activityLabelsChResponse){
				if(pp==cc)
				{
					pp=0;
					break;
				}else{
					pp++;
				}
				if(!second.equals(event) && !second.equals(previous)){
					int noviol = 0;
					HashMap<String, Integer> secondEl = new  HashMap<String, Integer>();
					if(violatedForThisTrace.containsKey(previous)){
						secondEl = violatedForThisTrace.get(previous);
						if(secondEl.containsKey(second)){
							noviol = secondEl.get(second);
						}
					}
					//violation
					//HF(second+"%"+event, createInstance(second+"%"+event, 1), modello);
					fulf = false;
					nr++;
					currentBucket = nr/bucketWidth;
					//modello.addObservation( , event, currentBucket, bucketWidth, fulf);
					if(nr>1 && nr<50){
						start2 = System.currentTimeMillis();
						mc = mod.addObservation(second, event, myAttr, attribute, attIndex, 1, mc);
						stop2 = System.currentTimeMillis();
						time = time+stop2-start2;
						en++;
						nr=1;	
					}
					secondEl.put(second, noviol+1);
					violatedForThisTrace.put(previous,secondEl);
					violatedConstraintsPerTraceCh.putItem(caseId, violatedForThisTrace);
				}
			}
		}

		//update the counter for the current trace and the current event
		//**********************

		int numberOfEvents = 1;
		if(!counter.containsKey(event)){
			counter.put(event, numberOfEvents);
		}else{
			numberOfEvents = counter.get(event);
			numberOfEvents++;
			counter.put(event, numberOfEvents); 
		}
		activityLabelsCounterChResponse.putItem(caseId, counter);
		lastActivity.putItem(caseId, event);
		//***********************
		
		if(en==199999){ //instanceForTree.get(name).numInstances()>1000
			double fulfill = 0.0;
			double act = 0.0;
			for(String caseID : activityLabelsCounterChResponse.keySet()) {
				HashMap<String, Integer> Counter = activityLabelsCounterChResponse.getItem(caseID);
				HashMap<String, HashMap<String, Integer>> PendingForThisTrace = fulfilledConstraintsPerTraceCh.getItem(caseID);
				
				if(Counter.containsKey("b-null")){
					double totnumber = Counter.get("b-null");
					act = act + totnumber;
					if(PendingForThisTrace.containsKey("b-null")){
						if(PendingForThisTrace.get("b-null").containsKey("a-null")){
							double stillpending = PendingForThisTrace.get("b-null").get("a-null");
							fulfill = fulfill + stillpending;
						}
					}
				}
			}
			
			for(String prHF : modello.hoeffCollection.keySet()){ 				
				ComputeKPI ckpi = new ComputeKPI();
				Measurement[] measurement = modello.hoeffCollection.get(prHF).getModelMeasurements();
				if(measurement[0].getValue() >= 1){
					try {
//						printout.println("@@@@@@@@@@@@\n"+prHF+"\n"+modello.hoeffCollection.get(prHF).toString());
						double[] out = ckpi.ComputeKPI(modello.hoeffCollection.get(prHF).toString());
//						printout.println("act: "+out[0]+"\tratio: "+out[1]);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}			
			System.out.println("ChResp"+"\t"+fulfill+"\t"+(act-fulfill));
//			printout.flush();
//			printout.close();
//			for(String name : numberViolFul.keySet()){
//				printout.println("@@@@@@@@@@@@\n"+name+"\t"+numberViolFul.get(name).getFirst()+", "+numberViolFul.get(name).getSecond());
//			}
		}
		//System.out.println(en);
//		System.out.println("ChRe:\ttprocess:\t"+(System.currentTimeMillis()-start)+"\ttaddObs:\t"+time);
		printout.println(System.currentTimeMillis()-start);
		printout.flush();
//		printout.close();
	}
	
	public void HF(String name, Instance instance, Model modello){
		if(!modello.hoeffCollection.containsKey(name)){ //instanceForTree.get(name).numInstances()>=5 && 
			HoeffdingTree hf = new HoeffdingTree();							
			
				Instances in = instanceForTree.get(name);
				InstancesHeader ih = new InstancesHeader(in);
				in.setClassIndex(0);

				Attribute prova = in.attribute("class");

				hf.prepareForUse();
				
				hf.setModelContext(ih);
				hf.leafpredictionOption.setChosenLabel("MC");
				hf.gracePeriodOption.setValue(2);
				hf.splitConfidenceOption.setValue(1.0E-2);
				
				hf.trainOnInstance(instance);
				modello.hoeffCollection.put(name, hf);

		}else if(modello.hoeffCollection.containsKey(name)){ //instanceForTree.get(name).numInstances()>numInstUpdate && 
			
//				hoeffCollection.get(name).updateClassifier(instanceForTree.get(name).instance(instanceForTree.get(name).numInstances()-1)); //instanceForTree.get(name).instance(instanceForTree.get(name).numInstances()-1)
				Instances in = instanceForTree.get(name);
				
				in.setClassIndex(0);
				modello.hoeffCollection.get(name).trainOnInstance(instance);
				
//				modello.hoeffCollection.get(name).updateClassifier(instanceForTree.get(name).instance(instanceForTree.get(name).numInstances()-1)); //instanceForTree.get(name).instance(instanceForTree.get(name).numInstances()-1)
				instanceForTree.get(name).delete(instanceForTree.get(name).numInstances()-1);
			
		}
	}
	
	public static boolean isNumeric(String str)  
	{  
		try  
		{  
//			double d = Double.parseDouble(str);  
			Double.parseDouble(str);
		}  
		catch(NumberFormatException nfe)  
		{  
			return false;  
		}  
		return true;  
	}
	
	public Instance createInstance(String name, int classAt){

		Instances instse;
		if(!instanceForTree.containsKey(name)){
			instse = new Instances(name, myAttr, 1000);
		}else{
			instse = instanceForTree.get(name);
		}
		//		double[] instanceValue = new double[myAttr.size()];
		int n=0;
		InstancesHeader ih = new InstancesHeader(instse);
		DenseInstance instance = new DenseInstance(66);
		instance.setDataset(ih);
		for(Attribute attr : myAttr){	
			String attrName = attr.name();
			if(attrName.contains("class")){
//				instanceValue[n] = classAt;
				instance.setValue(n, classAt);
			}else{
				if(attribute.containsKey(attrName)){
					//System.out.println(isNumeric(attribute.get(attrName).toString()));
					if(!isNumeric(attribute.get(attrName).toString())|| attrName.equals("Activity code") || attrName.equals("Specialism code") ){//
						//					instanceValue[n] = attIndex.get(attrName);

						instance.setValue(attr, attIndex.get(attrName));

					}else{
						double tt = (double) attribute.get(attrName);
						//					instanceValue[n] = tt; //(double) attribute.get(attrName);
						instance.setValue(n, tt);
					}
				}

				try{
					double tt = (double) attribute.get(attrName);
					instance.setValue(n, tt);
					//System.out.println(tt);
				}catch(Exception e) {
					instance.setValue(n, 88.88);
					//System.out.println(attribute.get(attrName));
				}
			}
			n++;
		}
		
		instse.add(instance);
		//		System.out.println(instse);
		instanceForTree.put(name, instse);

		return instance;
	}

//	@Override
//	public void updateModel(DeclareModel d) {
//		for(String param1 : activityLabelsChResponse){
//			for(String param2 : activityLabelsChResponse){
//				if(!param1.equals(param2)){
//					
//					double fulfill = 0;
//					double act = 0;
//					for(String caseId : activityLabelsCounterChResponse.keySet()) {
//						HashMap<String, Integer> counter = activityLabelsCounterChResponse.getItem(caseId);
//						HashMap<String, HashMap<String, Integer>> fulfillForThisTrace = fulfilledConstraintsPerTraceCh.getItem(caseId);
//
//						if(counter.containsKey(param1)){
//							double totnumber = counter.get(param1);
//							act = act + totnumber;
//							if(fulfillForThisTrace.containsKey(param1)){
//								if(fulfillForThisTrace.get(param1).containsKey(param2)){	
//									double currentFullfill = fulfillForThisTrace.get(param1).get(param2);
//									fulfill = fulfill + currentFullfill;
//								}
//							}
//						}
//					}
//					d.addChainResponse(param1, param2, act, fulfill);
//				}
//			}
//		}
//	}

	@Override
	public Integer getSize() {
		return activityLabelsChResponse.size() +
				activityLabelsCounterChResponse.getSize() +
				fulfilledConstraintsPerTraceCh.getSize() +
				1; // this is for the lastActivity field
	}

}
