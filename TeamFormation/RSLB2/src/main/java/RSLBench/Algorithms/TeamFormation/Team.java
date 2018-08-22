package RSLBench.Algorithms.TeamFormation;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class Team {
	public int m_k;
	public int m_cost;
	public LinkedList<Agent> m_content;
	public Hashtable<Task, Integer> m_taskCount;
	public Mission m_mission;
	
	public Team (){
		m_k = -1;
		m_cost = 0;
		m_content = new LinkedList<Agent>();
		m_taskCount = new Hashtable<Task,Integer>();
	}
	
	public Team(Team t){
		m_k = t.m_k;
		m_cost = t.m_cost;
		m_content = new LinkedList<Agent>(t.m_content);
		m_mission =  new Mission(t.m_mission);
		m_taskCount = new Hashtable<Task,Integer>(t.m_taskCount);
	}
	
	public Team (Mission mission){
		//m_k = -1;
        m_k = -1-mission.getRobustness();
		m_cost = 0;
		m_content = new LinkedList<Agent>();
		m_taskCount = new Hashtable<Task,Integer>();
		m_mission = mission;
		
		for (int i = 0; i < m_mission.size(); i++) {
			m_taskCount.put(m_mission.get_task(i), -1-mission.getRobustness());
            //m_taskCount.put(m_mission.get_task(i), -1);
		}
	}

	public Mission getMission(){
		return m_mission;
	}
	
	public Agent getAgent(int index){
		return m_content.get(index);
	}

	public int getNbAgent(){
		return m_content.size();
	}

	public void addAgent(Agent a){
		if (!m_taskCount.isEmpty() && !m_content.contains(a)) {
			//ajout des skills de l'agent
			
			for (Iterator iterator = a.iterator(); iterator.hasNext();) {
				Task task = (Task) iterator.next();
				if (m_taskCount.containsKey(task)) {
					int value = m_taskCount.get(task);
					//System.out.println("Valeur: "+value);
					value++;
					
					m_taskCount.put(task, value);
				}
			}
			
			//Calculm de K
			computeTeamRobustness();
            
			//System.out.println("Kmin fin: "+ kMin);
			//calcul du nouveau coup
			m_cost += a.get_cost(m_mission.get_id());
			//ajout de l'agent
			m_content.add(a);
		}
	}
	
	public void removeAgent(Agent a){
		if(m_content.contains(a)){
			//ajout des skills de l'agent
			for (Iterator iterator = a.iterator(); iterator.hasNext();) {
				Task task = (Task) iterator.next();
				if (m_taskCount.containsKey(task)) {
					int value = m_taskCount.get(task);
					
					value--;
					
					m_taskCount.put((Task) task, value);
				}
			}
			
			//Calculm de K
			computeTeamRobustness();
            
			//calcul du nouveau coup
			m_cost -= a.get_cost(m_mission.get_id());
			//suppression de l'agent
			//System.out.println("suppression");
			m_content.remove(a);
		}
	}
	
	public void removeLastAgent(){
		Agent a = m_content.getLast();
		//ajout des skills de l'agent
		for (Iterator iterator = a.iterator(); iterator.hasNext();) {
			Task task = (Task) iterator.next();
			if (m_taskCount.containsKey(task)) {
				int value = m_taskCount.get(task);
				
				value--;
				
				m_taskCount.put((Task) task, value);
			}
		}
		
		//Calculm de K
        computeTeamRobustness();
        
		//calcul du nouveau coup
		m_cost -= a.get_cost(m_mission.get_id());
		//suppression de l'agent
		m_content.removeLast();
	}
	
	public boolean dominates(Team t){
		return (m_cost <= t.m_cost && m_k > t.m_k || m_cost < t.m_cost && m_k >= t.m_k || m_cost == t.m_cost && m_k == t.m_k);
	}
	
	public boolean usefullAgent(Agent a){
		
		return a.isAbleToPerform(m_mission);
	}
	
	public boolean isEmpty() {
		return m_content.isEmpty();
	}
	
	public boolean equals(Team obj) {
		
		if (m_cost != obj.m_cost ) {
			return false;
		}
		
		if (m_k != obj.m_k) {
			return false;
		}
		
		if (!m_content.equals(obj.m_content)) {
			return false;
		}
		
		if (!m_mission.equals(obj.m_mission)) {
			return false;
		}
		
		if (!m_taskCount.equals(obj.m_taskCount)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String s = m_content.toString() + " k:" + (m_k) + " cost:" + m_cost;
		return s;
	}
    
    private void computeTeamRobustness(){
        Set<Task> setTask = m_taskCount.keySet();
        Iterator<Task> itr = setTask.iterator();
        Task t = itr.next();
        int kMin = m_taskCount.get(t);
        //System.out.println("Kmin debut: " + kMin);
        while (itr.hasNext()) {
            t = itr.next();
            if (m_taskCount.get(t) < kMin) {
                kMin = m_taskCount.get(t);
            }	
        }
        
        m_k = kMin;
    }
}
