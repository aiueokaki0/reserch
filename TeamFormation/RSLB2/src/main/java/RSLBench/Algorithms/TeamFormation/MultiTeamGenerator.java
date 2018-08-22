package RSLBench.Algorithms.TeamFormation;

import java.util.ArrayList;
import java.util.Hashtable;

public class MultiTeamGenerator {
	private ArrayList<TeamSet> m_stock;
	private Agent[] m_agents;
	private Mission[] m_missions;
	private int[] m_atribution;
	private TeamSet m_currentSet;
	public double m_nbBactrack=0;
	private int m_costMax = -1;
	private int m_kMax = -1;
	
	public MultiTeamGenerator (Mission[] missions, Agent[] agents){
		
		m_stock = new ArrayList<TeamSet>();
		m_agents = agents;
		m_missions = missions;
		m_atribution = new int[agents.length];
		
		m_currentSet = new TeamSet(missions, agents);
		
		for (int i = 0; i < m_atribution.length; i++) {
			m_atribution[i] = -1;
		}
		
		//for (int i = 0; i < agents.length; i++) {
			//System.out.println("agent: " + agents[i].toString());
			
		//add(0);
		//}
		
		/*System.out.println();
		System.out.println();
		System.out.println(m_stock);
		System.out.println("NB prune: " + m_nbBactrack);
		System.out.println("nb set valide: " + m_stock.size());*/
	}
	
	public void solve (int cost, int k){
		m_kMax = k;
		m_costMax = cost;
		add(0);
	}
	
	public void solve (){
        System.out.println("JE SUIS LE BNB !!!!!!");
		add(0);
	}
	
	private void add (int agentIndex){
		
		//Appel recursif
		if (agentIndex >= m_agents.length) {
			if (!m_currentSet.isValid()) {
				return;
			}
			boolean goodSetOfTeam = true;
			for (int j = 0; j < m_stock.size(); j++) {
				if (m_currentSet.dominates(m_stock.get(j))) {
					m_stock.remove(j);
					j--;
				}
				else if (m_stock.get(j).dominates(m_currentSet)) {
					goodSetOfTeam = false;
					break;
				}
			}
			
			if (goodSetOfTeam) {
				m_stock.add(new TeamSet(m_currentSet));
			}
			return;
		}
		
		for (int numTeam = 0; numTeam < m_missions.length+1; numTeam++) {
			if(numTeam == 0){
				m_atribution[agentIndex] = numTeam;
				add(agentIndex+1);
			}else if (m_currentSet.usefullAgent(numTeam-1, m_agents[agentIndex])) {
					
				m_atribution[agentIndex] = numTeam;
					
				//update
				m_currentSet.addAgent(numTeam-1, m_agents[agentIndex]);
				/*	
				//heuristique
				if (m_currentSet.get_cost() > m_costMax){ 
					m_nbBactrack+=Math.pow((m_missions.length+1), (m_agents.length-agentIndex));
					continue;
				}*/
				add(agentIndex+1);
					
				m_currentSet.removeAgent(numTeam-1, m_agents[agentIndex]);
				m_atribution[agentIndex] = -1;
					
			}
			else{
				//m_nbBactrack+=Math.pow((m_missions.length+1), (m_agents.length-agentIndex));
				m_nbBactrack++;
			}
		}
	}
	
	public ArrayList<TeamSet> getAllSetOfTeams(){
		return m_stock;
	}


	public void localSearch(int[] oldCandidate){
		TeamSet previousTeamSet = new TeamSet(m_missions, m_agents, oldCandidate);
		int[] newCandidate = oldCandidate;

		for(int i = 0 ; i < newCandidate.length ; i++){
			for(int j = 0; j < m_missions.length; j++ ){
				int previousValue = newCandidate[i];
				newCandidate[i] = j;

				TeamSet newTeamSet = new TeamSet(m_missions, m_agents, newCandidate);

				if(newTeamSet.dominates(previousTeamSet)){
					System.out.println("newTeam k:"+newTeamSet.get_k()+" cost: "+newTeamSet.get_cost()+" domine previous team k:"+previousTeamSet.get_k()+"cost: "+previousTeamSet.get_cost());
					previousTeamSet = newTeamSet;
				}
				else{
					newCandidate[i] = previousValue;
					System.out.println("newTeam k:"+newTeamSet.get_k()+" cost: "+newTeamSet.get_cost()+" domine PAS previous team k:"+previousTeamSet.get_k()+"cost: "+previousTeamSet.get_cost());
				}
			}
		}

		m_stock.add(previousTeamSet);
	}

	public void debileRandom(){
		TeamSet teamSet = new TeamSet(m_missions, m_agents);
		
		int Min2 = 0;
		int Max2 = m_missions.length;
		int randomMissionIndex = 0;

		for(int i = 0 ; i < m_agents.length ; i++){
			//On selectionne une mission au hasard
			randomMissionIndex = Min2 + (int)(Math.random() * ((Max2 - Min2)));
			teamSet.addAgent(randomMissionIndex, m_agents[i]);
		}

		m_stock.add(teamSet);
	}

	public void localSearchRandomWalk(int[] oldCandidate){
        //System.out.println("TAILLE OLD: "+ oldCandidate.length);
        System.out.println("JE SUIS LE LOCAL SEARCH");
		TeamSet previousTeamSet = new TeamSet(m_missions, m_agents, oldCandidate);
		int[] newCandidate = oldCandidate;
		boolean amelio = true;
        
        int Min = 0;
        int Max = m_agents.length;
        
        int Min2 = 0;
        int Max2 = m_missions.length;
        
        int nbTry = 0;
        int maxTry = 100;
        
        // Phase 1
		do{
            //On selectionne un agent au hasard
            int i = Min + (int)(Math.random() * ((Max - Min)));
            int nbtry2 = 0;
            int j = 0;
            
           // boolean ok = true;
           // do{
                //On selectionne une mission au hasard
                j = Min2 + (int)(Math.random() * ((Max2 - Min2)));
                //nbtry2++;
                //if(previousTeamSet.get_team(j).m_k >= 0){
                //    ok = false;
                //}
                //System.out.println("test");
            //}while(!ok && nbtry2 < 10);
            
            
            int previousValue = newCandidate[i];
            newCandidate[i] = j;

            TeamSet newTeamSet = new TeamSet(m_missions, m_agents, newCandidate);

            if(newTeamSet.dominates(previousTeamSet)){
                //System.out.println("newTeam k:"+newTeamSet.get_k()+" cost: "+newTeamSet.get_cost()+" domine previous team k:"+previousTeamSet.get_k()+"cost: "+previousTeamSet.get_cost());
                previousTeamSet = newTeamSet;
                nbTry = 0;
            }
            else{
                newCandidate[i] = previousValue;
                //System.out.println("newTeam k:"+newTeamSet.get_k()+" cost: "+newTeamSet.get_cost()+" domine PAS previous team k:"+previousTeamSet.get_k()+"cost: "+previousTeamSet.get_cost());
                nbTry++;
            }
		}while(nbTry < maxTry);
        
        // Phase 2
        // Check all direct neighbour
        do{
            amelio = false;
            for(int i = 0 ; i < m_agents.length ; i++){
                for(int j = 0; j < m_missions.length; j++ ){
                    /*
                    if(previousTeamSet.get_team(j).m_k >= 0){
                        System.out.println("robustess attein pour la mission : "+ j);
                        continue;
                    }
                    //*/
                    int previousValue = newCandidate[i];
                    newCandidate[i] = j;
                
                    TeamSet newTeamSet = new TeamSet(m_missions, m_agents, newCandidate);
                    
                    //System.out.println("new team set: "+newTeamSet);
                    //System.out.println("Previous: "+previousTeamSet);
                    
                    if(newTeamSet.dominates(previousTeamSet)){
                        //System.out.println("newTeam k:"+newTeamSet.get_k()+" cost: "+newTeamSet.get_cost()+" domine previous team k:"+previousTeamSet.get_k()+"cost: "+previousTeamSet.get_cost());
                        previousTeamSet = newTeamSet;
                        amelio= true;
                        break;
                    }
                    else{
                        newCandidate[i] = previousValue;
                        //System.out.println("newTeam k:"+newTeamSet.get_k()+" cost: "+newTeamSet.get_cost()+" domine PAS previous team k:"+previousTeamSet.get_k()+"cost: "+previousTeamSet.get_cost());
                    }
                }
                
                if(amelio){
                    break;
                }
                
            }
        }while(amelio);
        
		m_stock.add(previousTeamSet);
	}
    
    /*
    public void localSearchRandomWalk2(int[] oldCandidate){
        //System.out.println("TAILLE OLD: "+ oldCandidate.length);
        System.out.println("JE SUIS LE LOCAL SEARCH2");
		
        //Init
        int nbTry=0;
        int maxNeighbour = 100;
        
        TeamSet cadidate = null;
        
        if(oldCandidate == null){
            cadidate = generateRadomTeamSet();
        }else{
            cadidate = new TeamSet(m_missions, m_agents, oldCandidate);
        }
        
        //Phase1
        do{
            m_currentSet = new TeamSet(cadidate);
            
            do{
                mutation();
            }while(!m_currentSet.isValid());
            
            if(m_currentSet.dominates(cadidate)){
                cadidate = m_currentSet;
                nbTry=0;
            }else{
                nbTry++;
            }
            
        }while(nbTry<maxNeighbour);
        
    
        //Phase2
        boolean improvement;
        do{
            m_currentSet = new TeamSet(cadidate);
            improvement=false;
            
            for(int i = 0 ; i < m_agents.length ; i++){
                
                //Si l'agent est déja assigné on l'enlève de la team
                if(m_currentSet.getTeam(m_agents[i]) != -1){
                    m_currentSet.removeAgent(m_currentSet.getTeam(m_agents[i]), m_agents[i]);
                }
                
                for(int j = 0 ; j < m_missions.length ; j++){
                    //On l'agent a une team
                    
                    m_currentSet.addAgent(j, m_agents[i]);
                    
                    
                    if(m_currentSet.dominates(cadidate)){
                        cadidate = m_currentSet;
                        improvement=true;
                        break;
                    }
                }
                
                if(improvement){
                    break;
                }
                
            }
            
        }while(improvement);
        
		m_stock.add(cadidate);
	}
    //*/

    /*
    private TeamSet generateRadomTeamSet(){
        TeamSet teamSet = new TeamSet(m_missions, m_agents);
        int nbAgentAssign = 0;
        int nbMissionSatified = 0;
        
        Hashtable<Agent, Integer> tmpAgents = new Hashtable<Agent, Integer>();
		for (int i = 0; i < m_agents.length ; i++) {
			tmpAgents.put(m_agents[i], 0);
		}
        
        int Min1 = 0;
        int Max1 = m_agents.length;
        
        int Min2 = 0;
        int Max2 = m_missions.length;
        
        
        //Temps que tous le monde n'a pas été assigné
        while(nbAgentAssign < m_agents.length){
            
            int randomAgentIndex = Min1 + (int)(Math.random() * ((Max1 - Min1)));
            int randomMissionIndex = Min2 + (int)(Math.random() * ((Max2 - Min2)));
            Agent a=m_agents[randomAgentIndex];
            
            //si l'agent n'a pas déja été attribué
			if (tmpAgents.get(a)==0) {
				//Si l'agent est utile pour la mission
				if (teamSet.get_team(randomMissionIndex).usefullAgent(a)) {
                    //Si le k requit est atteind on l'ajoute pas
                    if(teamSet.get_team(randomMissionIndex).m_k < 0 || nbMissionSatified == m_missions.length){
                        //on associe l'agent a la mission
                        teamSet.addAgent(randomMissionIndex, a);
                        //On le marque comme attribué
                        tmpAgents.put(a,1);
                        nbAgentAssign++;
                    }
                    else{
                        nbMissionSatified++;
                    }
				}
            }
            
        }
        
        return teamSet;
    }
    //*/
    
    /*
    public void localSearchRandomWalk2(int[] oldCandidate){
		TeamSet teamSet = new TeamSet(m_missions, m_agents);
        if(oldCandidate.length!=0){
            TeamSet previousTeamSet = new TeamSet(m_missions, m_agents, oldCandidate);
            m_currentSet = new TeamSet(previousTeamSet);
            
            
            
            if(m_currentSet.dominates(previousTeamSet)){
                m_stock.remove(teamSet);
                teamSet = m_currentSet;
            }
        }
        else{
            int Min2 = 0;
            int Max2 = m_missions.length;
            int randomMissionIndex = 0;
            
            //On assigne tous les agent
            for(int i = 0 ; i < m_agents.length ; i++){
                //On selectionne une mission au hasard
                randomMissionIndex = Min2 + (int)(Math.random() * ((Max2 - Min2)));
                teamSet.addAgent(randomMissionIndex, m_agents[i]);
            }
        }
		
		m_stock.add(teamSet);
	}
    //*/
    
    /*
    private void mutation(){
		
		int randomAgentIndex = 0 + (int)(Math.random() * ((m_agents.length - 0)));
		int randomMissionIndex = 0 + (int)(Math.random() * ((m_missions.length - 0)));
		
		//Si l'agent est déja assigné on l'enlève de la team
		if(m_currentSet.getTeam(m_agents[randomAgentIndex]) != -1){
			m_currentSet.removeAgent(m_currentSet.getTeam(m_agents[randomAgentIndex]), m_agents[randomAgentIndex]);
		}
		
		//On l'agent a une team
		if(randomMissionIndex != -1){
			m_currentSet.addAgent(randomMissionIndex, m_agents[randomAgentIndex]);
		}
	}
    //*/

	
}
