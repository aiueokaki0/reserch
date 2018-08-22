package RSLBench.Algorithms.TeamFormation;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.ArrayList;


public class Agent implements Iterable<Task>{
	private int m_id;
	private ArrayList<Double> m_cost;
	private LinkedHashSet<Task> m_skills;
	
	public Agent ()
	{
		m_id = -1;
		m_cost = new ArrayList<Double>();
		m_skills = new  LinkedHashSet<Task>();
	}
	
	public Agent (int id, ArrayList<Double> cost){
		m_id = id;
		m_cost = cost;
		m_skills = new LinkedHashSet<Task>();
	}
	
	public Agent(int id, ArrayList<Double> cost, LinkedHashSet<Task> skills)
	{
		m_id = id;
		m_cost = cost;
		m_skills = skills;
	}
	
	public int get_id() {
		return m_id;
	}
	
	public ArrayList<Double> get_cost() {
		return m_cost;
	}
	
	public Double get_cost(int id) {
		return m_cost.get(id);
	}

	public int get_nbSkills(){
		return m_skills.size();
	}
	
	public void set_cost(ArrayList<Double> cost){
		m_cost = cost;
	}
	
	/*public LinkedHashSet<Task> get_skills() {
		return m_skills_map;
	}*/
	
	public void addSkill(Task s){
		m_skills.add(s);
		//System.out.println(m_skills.size());
	}
	
	public void removeSkill(Task s){
		m_skills.remove(s);
	}
	
	public boolean isAbleToPerfomTask(Task t){
		return m_skills.contains(t);
	}
	
	public String toString() {
		String s = new String();
		
		s += m_id;
		/*s +="[";
		for(int i = 0; i < m_cost.size(); i++){
			s += " "+(float)((int)(m_cost.get(i)*100.0))/100.0;
		}
		s += "]";*/
		
		return s;
	}

	/**
	 * Say if the agent have skill to perform the mission m
	 * @param m mission
	 * @return true if the agent is able to do the mission else false
	 */
	public boolean isAbleToPerform(Mission m){
		
		if (m_skills.isEmpty()) {
			return false;
		}
		
		for (int i = 0; i < m.size() ; i++ ) {
			//System.out.println("task: "+m.get_task(i));
			if (m_skills.contains(m.get_task(i))) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public Iterator<Task> iterator() {
		// TODO Auto-generated method stub
		return m_skills.iterator();
	}
	
	
}
