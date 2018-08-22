/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RSLBench.Algorithms.TeamFormation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import RSLBench.Assignment.Assignment;
import RSLBench.Assignment.DCOP.DCOPAgent;
import RSLBench.Assignment.DCOP.DCOPSolver;
import RSLBench.CenterAgent;
import RSLBench.CivilianAgent;
import RSLBench.Constants;
import RSLBench.PlatoonAmbulanceAgent;
import RSLBench.PlatoonPoliceAgent;
import RSLBench.Algorithms.BMS.BinaryMaxSum;
import RSLBench.Helpers.Utility.ProblemDefinition;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;

import rescuecore2.standard.entities.StandardEntityURN;
import static rescuecore2.standard.entities.StandardEntityURN.FIRE_BRIGADE;

import rescuecore2.worldmodel.EntityID;
import RSLBench.Helpers.Distance;
import RSLBench.Helpers.Logging.Markers;
import rescuecore2.standard.entities.StandardWorldModel;
import rescuecore2.standard.entities.AmbulanceCentre;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.Civilian;
import rescuecore2.standard.entities.Human;
import rescuecore2.standard.entities.Refuge;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityConstants;
import RSLBench.Helpers.Utility.FirstUtilityFunction;
import java.util.concurrent.TimeUnit;

public class TF extends DCOPSolver {
    private static final Logger Logger = LogManager.getLogger(BinaryMaxSum.class);

    /**
     * The damping factor to employ.
     */
    public static final String KEY_MAXSUM_DAMPING = "maxsum.damping";
    private int cpt = 0;
    private int[] m_oldSet = null;
    private int[] m_oldSetAmb = null;
    private TeamSet m_oldTeamSet = null;

    @Override
    protected DCOPAgent buildAgent(StandardEntityURN type) {
        final boolean team = config.getBooleanValue(Constants.KEY_INTERTEAM_COORDINATION);
        //cpt++;
        //System.out.println("SALUT: " + cpt);
        //System.exit(0);
        switch(type) {
            case FIRE_BRIGADE:
                return team ? new BMSTeamFireAgent() : new BMSFireAgent();
            case POLICE_FORCE:
                return team ? new BMSTeamPoliceAgent() : new BMSPoliceAgent();
            default:
                throw new UnsupportedOperationException("The Binary Max-Sum solver does not support agents of type " + type);
        }
    }

    @Override
    public String getIdentifier() {
        return "TeamFormation";
    }

    @Override
    public List<String> getUsedConfigurationKeys() {
        List<String> result = super.getUsedConfigurationKeys();
        result.add(KEY_MAXSUM_DAMPING);
        return result;
    }

    public Assignment compute(ProblemDefinition problem) {

        Assignment finalAssignment = new Assignment();

        ArrayList<EntityID> lesAgentsFires = problem.getFireAgents();
        ArrayList<EntityID> lesFires = problem.getFires();
        
        ArrayList<EntityID> lesAgentsPolice = problem.getPoliceAgents();
        ArrayList<EntityID> lesBlockades = problem.getBlockades();
        
        ArrayList<EntityID> lesAgentsAmbulance = problem.getAmbulanceAgents();
        ArrayList<EntityID> lesCivilians = problem.getCivilians();

        ArrayList<ArrayList<Double>> utilityMat = new ArrayList<ArrayList<Double>>(lesAgentsFires.size());

        ArrayList<ArrayList<Double>> utilityMatAmbulance = new ArrayList<ArrayList<Double>>(lesAgentsAmbulance.size());

        Task fireTask = new Task(0);
        
        Task civilianTask = new Task(1);

        Agent[] teamFormationAgent = new Agent[lesAgentsFires.size()];
        
        Agent[] teamFormationAgentAmbulance = new Agent[lesAgentsAmbulance.size()];
        
        /*
        //HACK
        if(lesFires.size() < 5)
        {
            try {
                Thread.sleep(1000000);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        */

        //System.out.println("Création des Agents");
        // 1) Création des agent pour le teamFormation
        //------------------------------------------------------------------
        int idAgent = 0;
        StandardWorldModel world = problem.getWorld();
        if(!lesFires.isEmpty()){
            for (EntityID agent : lesAgentsFires) {
                ArrayList<Double> util = new ArrayList<Double>(lesFires.size());
                for (EntityID fire : lesFires) {
                    //util.add(problem.getFireUtility(agent, fire));
                    //double distance = Distance.humanToBuilding(agent, target, world);
                    util.add(Distance.humanToBuilding(agent, fire, world));

                }
                Agent a = new Agent(idAgent,util);
                a.addSkill(fireTask);
                teamFormationAgent[idAgent] = a;

                utilityMat.add(util);
                finalAssignment.assign(agent, lesFires.get(0));
                idAgent++;
            }
        }
        //System.out.println("NB Agent: "+idAgent);

        idAgent = 0;

        Collection<StandardEntity> e = problem.getWorld()
                .getEntitiesOfType(StandardEntityURN.CIVILIAN);
        List<EntityID> civs = new ArrayList<>();
        for (StandardEntity next : e) {
            if (next instanceof Civilian) {
                Civilian b = (Civilian) next;
                //Logger.info("{} - {} - {} - {} ",b.getID(), CivilianAgent.wounded.get(b.getID()), b.getPosition(problem.getWorld()).getClass(), CivilianAgent.rescued.get(b.getID()));
            	if(CivilianAgent.wounded.get(b.getID()) && !(b.getPosition(problem.getWorld()) instanceof Refuge) && !(b.getPosition(problem.getWorld()) instanceof AmbulanceCentre)) {
            		if(!CivilianAgent.rescued.get(b.getID())) {
                		civs.add(b.getID());
                	}
            	}
            }
        }
        Logger.info("CIVS SIZE : {}",civs.size());
        
        if(!civs.isEmpty()){
        	/*for (PlatoonAmbulanceAgent agent : CenterAgent.ambulanceAgents) {
        		double best = -1;
        		EntityID idbest = new EntityID(-1);
        		   
        		
        		//if(!agent.listHelp.isEmpty()) {
        		//	idbest = problem.getHighestTargetForPoliceAgent(agent.getID(), agent.listHelp);
        		//	Logger.info(Markers.RED,"### AGENT " + agent.getID() + " BARRICADE ASSIGNEE VIA HELP : "+idbest);
        		//}
        		//else {
        		
                    for (EntityID block : civs) {
                    	double util = problem.getAmbulanceUtility(agent.getID(), block);
                    	if(util > best) {
                    		best = util;
                    		idbest = block;
                    	}
                    }     		
        		//}
                finalAssignment.assign(agent.getID(), idbest);
            }*/
        	ArrayList<EntityID> alreadyAssigned = new ArrayList<EntityID>();
        	for(EntityID block : civs) {
	        	 Map<EntityID, Double> map = new HashMap<>();
	             for (PlatoonAmbulanceAgent agent : CenterAgent.ambulanceAgents) {
	            	 if(!agent.withCivilian && !alreadyAssigned.contains(agent.getID())) {
	            		 map.put(agent.getID(), problem.getAmbulanceUtility(agent.getID(), block));
	            	 }
	             }
	             List<EntityID> res = ProblemDefinition.sortByValue(map);
	             finalAssignment.assign(res.get(0), block);
        		 alreadyAssigned.add(res.get(0));
        	}
        }
        
        // On assigne aux policiers le block le plus proche
        if(!lesBlockades.isEmpty()) {
        	ArrayList<EntityID> alreadyAssigned = new ArrayList<EntityID>();
        	for (PlatoonPoliceAgent agent : CenterAgent.policeAgents) {
        		double best = -1;
        		EntityID idbest = new EntityID(-1);
        		

    			ArrayList<EntityID> remaining1 = new ArrayList<EntityID>(agent.listHelp);
    			remaining1.removeAll(alreadyAssigned);
    			
        		if(!remaining1.isEmpty()) {
                    /*for (EntityID block : agent.listHelp) {
                    	double util = problem.getPoliceUtility(agent.getID(), block);
                    	if(util > best) {
                    		best = util;
                    		idbest = block;
                    	}
                    }*/
        			idbest = problem.getHighestTargetForPoliceAgent(agent.getID(), remaining1);
        		}
        		else {
        			ArrayList<EntityID> remaining = new ArrayList<EntityID>(lesBlockades);
        			remaining.removeAll(alreadyAssigned);
                    for (EntityID block : remaining) {
                    	double util = problem.getPoliceUtility(agent.getID(), block);
                    	if(util > best) {
                    		best = util;
                    		idbest = block;
                    	}
                    }        			
        		}
                finalAssignment.assign(agent.getID(), idbest);
                alreadyAssigned.add(idbest);
            }
        }
        
        //System.out.println("Création des Missions");
        // 2) Création des Missions pour le teamFormation
        //------------------------------------------------------------------
        
        Mission[] teamFormationMissions = new Mission[lesFires.size()];

        int idMission = 0;
        int robustness = 0;
        

        Logger.info("NUMBER OF FIRES : {}",lesFires.size());
        for (EntityID fire : lesFires) {
            
            //Specify K to intensity
            Building b = (Building) world.getEntity(fire);
            int intensity = b.getFieryness();
            
            //*
            if(intensity == 1){
                robustness = 1;
            } else if(intensity == 2){
                robustness = 2;
            } else if(intensity == 3){
                robustness = 3;
            }
            	
            //*/
            //System.out.println("intensity = :" + intensity);
            
            Mission m = new Mission(idMission,robustness);
            //Mission m = new Mission(idMission);
            m.addTask(fireTask);
            teamFormationMissions[idMission]=m;
            idMission++;
        }
        //System.out.println("NB MISSIONs: "+idMission);

        
        
        //System.out.println("Solving");
        // 3) Solving
        //------------------------------------------------------------------
        
        // Initialization of candidate
        //*
        if(m_oldSet == null){
            m_oldSet = new int[lesAgentsFires.size()];
            
            int Min = 0;
            int Max = teamFormationMissions.length;
            
            for(int i = 0 ; i < teamFormationAgent.length ; i++){
                m_oldSet[i]= Min + (int)(Math.random() * ((Max - Min)));
            }
        }
        //*/
        
        MultiTeamGenerator mt = new MultiTeamGenerator(teamFormationMissions,teamFormationAgent);
        //*
        //if(m_oldSet.length == 0 && false){
        //mt.solve();
        //}else{
            //mt.localSearch(m_oldSet);
        mt.localSearchRandomWalk(m_oldSet);
        //mt.localSearchRandomWalk2(m_oldSet);
        //}
        //*/
        //mt.debileRandom();


        //System.out.println("Assignement");
        // 4) Extract best Assignment
        //------------------------------------------------------------------
        ArrayList<TeamSet> lesSolutions = mt.getAllSetOfTeams();
        if(!lesSolutions.isEmpty()){
            TeamSet solution = lesSolutions.get(0);
            
            /*if(m_oldTeamSet == null){
                m_oldTeamSet = new TeamSet(solution);
            }else if(m_oldTeamSet.get_k() == solution.get_k() && m_oldTeamSet.get_cost() == solution.get_cost()){
                solution = m_oldTeamSet;
                System.out.println("MEME SOLUTION");
            }*/
            
            //Find the best solution
            /*
            System.out.println("nb Solutions: "+ lesSolutions.size());
            for(int i =0 ; i < lesSolutions.size() ; i++){
                System.out.println("solution k: "+ solution.get_k() + "i k: "+ lesSolutions.get(i).get_k());
                if(solution.get_k() < lesSolutions.get(i).get_k()){
                    solution = lesSolutions.get(i);
                }
            }
            */
            
            m_oldSet = new int[lesAgentsFires.size()];
            
            //System.out.println(solution);
            //Convertion
            //*
            for(int i = 0 ; i < solution.get_nbTeam(); i++){
                Team t = solution.get_team(i);
                for (int j=0; j < t.getNbAgent() ; j++ ) {
                    //System.out.println("Index Agent: "+t.getAgent(j).get_id());
                    EntityID agentID = lesAgentsFires.get(t.getAgent(j).get_id());
                    //System.out.println("Index Mission: "+t.getMission().get_id());
                    EntityID fireID = lesFires.get(t.getMission().get_id());
                    
                    finalAssignment.assign(agentID, fireID);

                    m_oldSet[t.getAgent(j).get_id()] = t.getMission().get_id();
                }
                
            }
            //*/
            /*
            for(int i = 0 ; i < lesAgentsFires.size(); i++){
                finalAssignment.assign(lesAgentsFires.get(i), lesFires.get(0));
            }
            //*/

            /*
            for(int i = 0; i<m_oldSet.length; i++){
                System.out.println("m_oldSet["+i+"]="+m_oldSet[i]);
            }
            //*/

        }
        else{
            System.out.println("PAS DE SOLUTIONS");
        }
        
        
       
        
        //System.out.println("Exploration: "+ Math.pow(lesFires.size(), lesAgentsFires.size()));



        /*
        System.out.println("----------------------------");
        for(int i = 0; i < teamFormationAgent.size(); i++){
            System.out.print(teamFormationAgent.get(i) + " ");
        }
        System.out.println("----------------------------");
        //*/

        /*
        System.out.println("----------------------------");
        for(int i = 0; i < utilityMat.size(); i++){
            for (int j = 0; j< utilityMat.get(i).size(); j++ ) {
                System.out.print(" "+(float)((int)(utilityMat.get(i).get(j)*100.0))/100.0);
            }
            System.out.println();
        }
        System.out.println("----------------------------");
        //*/

        return finalAssignment;
    }

}
