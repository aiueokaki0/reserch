package RSLBench.Algorithms.TeamFormation;

import java.util.ArrayList;

public class TeamSet {
	private Team[] m_set;
	private Agent[] m_agents;
	private int m_cost = -1;
	private int m_k = -1;
	private int m_nbAgent = 0;
    //private ArrayList<Integer> m_assignement;
	
	public TeamSet(Mission[] missions, Agent[] agents){
		m_agents = agents;
        
        /*
        m_assignement = new ArrayList<Integer>(agents.length);
		for (int i = 0; i < m_agents.length; i++) {
			m_assignement.add(-1);
		}
        //*/

		m_set = new Team[missions.length];
		for (int i = 0; i < m_set.length; i++) {
			m_set[i] = new Team(missions[i]);
		}
	}
	
	public TeamSet(TeamSet ts) {
		
		m_agents = ts.m_agents.clone();
        
        /*
        m_assignement = new ArrayList<Integer>(ts.m_assignement);
		//*/

		m_set = new Team[ts.m_set.length];
		for (int i = 0; i < ts.m_set.length; i++) {
			m_set[i] = new Team(ts.m_set[i]);
		}
	
	}

	public TeamSet(Mission[] missions, Agent[] agents, int[] assignement){

		m_agents = agents;
		m_set = new Team[missions.length];
		for (int i = 0; i < m_set.length; i++) {
			m_set[i] = new Team(missions[i]);
		}

		/*
        m_assignement = new ArrayList<Integer>(agents.length);
        for (int i = 0; i < m_agents.length; i++) {
			m_assignement.add(-1);
		}
		//*/
        
		for(int i = 0; i < assignement.length; i++){
            if(assignement[i] < missions.length){
                addAgent(assignement[i],agents[i]);
                //m_assignement.set(i,assignement[i]);
            }
		}
	}
	
	public int get_cost(){
		return m_cost;
	}
	
	public int get_k(){
		return m_k;
	}
	
	public Team get_team(int index){
		return m_set[index];
	}

	public void decode(int I, int B){
		int rest;
		for (int i = 0; i < m_agents.length; i++) {
			rest = I % B;
			I = I / B;
			
			if (rest > 0) {
				m_set[rest-1].addAgent(m_agents[i]);
			}
		}	
	}
	
	public void addAgent(int indexTeam, Agent a){
		if (indexTeam < 0 || indexTeam > m_set.length) {
			System.out.println("Class TeamSet: indexTeam invalide ");
			return;
		}
        
        //m_assignement.set(a.get_id(), indexTeam);
        
		m_set[indexTeam].addAgent(a);
		m_cost += a.get_cost(m_set[indexTeam].getMission().get_id());
		updateK();
		m_nbAgent++;
	}
	
	public void removeAgent(int indexTeam, Agent a){
		if (indexTeam < 0 || indexTeam > m_set.length) {
			System.out.println("Class TeamSet: indexTeam invalide ");
			return;
		}
        
        //m_assignement.set(a.get_id(), -1);
        
		m_set[indexTeam].removeAgent(a);
		m_cost -= a.get_cost(m_set[indexTeam].getMission().get_id());
		updateK();
		m_nbAgent--;
	}
	
	public boolean usefullAgent(int indexTeam, Agent a){
		//System.out.println("Je test l'agent: "+ a + "pour la team :" + indexTeam);
		if (indexTeam < 0 || indexTeam > m_set.length) {
			System.out.println("TeamSet methode usefullAgent : indexTeam invalide");
			return false;
		}
		
		//System.out.println("result: " + m_set[indexTeam].usefullAgent(a));
		return m_set[indexTeam].usefullAgent(a);
		
	}
	
	/**
	 * Compare two team to know which is dominate
	 * @param ts team to compare
	 * @return true if the current team dominates the team in parameter
	 */
	public boolean dominates(TeamSet ts){
		/*if (m_set.length != ts.m_set.length) {
			System.out.println("Team pas de meme taille (fontion dominates TeamSet)");
		}*/

		double ratio1 = m_cost;
		double ratio2 = ts.m_cost; 

		//return m_k > ts.m_k && m_cost < ts.m_cost /*&& ratio1 < ratio2*/;

		/*
        for (int i = 0; i < m_set.length; i++) {
			if (!(m_set[i].dominates(ts.m_set[i]))) {
				return false;
			}
		}
		return true;
        //*/
        
        /*if (m_cost > ts.m_cost) {
			return false;
		}
        
        for (int i = 0; i < m_set.length; i++) {
			if(m_set[i].m_k < ts.m_set[i].m_k){
				return false;
			}
		}
		return true;*/
        return (m_cost <= ts.m_cost && m_k > ts.m_k || m_cost < ts.m_cost && m_k >= ts.m_k);
	}
	
	public boolean isValid(){
		/*for (int i = 0; i < m_set.length; i++) {
			if (m_set[i].m_k < 0) {
				return false;
			}
		}*/
		return m_nbAgent == m_agents.length;
	}
	
	public int get_nbTeam(){
		return m_set.length;
	}
	
	public String toString(){
		String s ="";
		s += "{";
		for (int j = 0; j < m_set.length; j++) {
			s+= m_set[j].toString();
			if (j != m_set.length-1) {
				s+=" ; ";
			}
		}
		s+="}";
		
		return s;
	}
	
	private void updateK(){
		
		if (m_set.length == 0) {
			return;
		}
		
		int negativeK = 0;

		int k = m_set[0].m_k;
		for (int i = 0; i < m_set.length; i++) {
			if(m_set[i].m_k < 0){
				negativeK+= -m_set[i].m_k;
			}
			else{
				if (m_set[i].m_k < k) {
					k = m_set[i].m_k;
				}
			}
		}

		if(negativeK > 0){
			m_k = -negativeK;
		}else{
			m_k = k;
		}
	}
	/*
    public int getTeam(Agent a){
		return m_assignement.get(a.get_id());
	}
	//*/
}
