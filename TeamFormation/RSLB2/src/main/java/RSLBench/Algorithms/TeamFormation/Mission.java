package RSLBench.Algorithms.TeamFormation;

import java.util.ArrayList;

public class Mission {
	private ArrayList<Task> m_tasks;
	private int m_id;
    private int m_robustness = 0;
	
	public Mission(int id){
		m_tasks = new ArrayList<Task>();
		m_id = id;
	}
	
    public Mission(int id, int robustness){
		m_tasks = new ArrayList<Task>();
		m_id = id;
        m_robustness = robustness;
	}
    
	public Mission (Mission m){
		m_tasks = new ArrayList<Task>(m.m_tasks);
		m_id = m.m_id;
        m_robustness = m.m_robustness;
	}
    
	public void addTask(Task task){
		m_tasks.add(task);
	}
	
	public void revomeTask(Task task){
		m_tasks.remove(task);
	}
	
	public int size(){
		return m_tasks.size();
	}
	
	public Task get_task(int index){
		return m_tasks.get(index);
	}

	public int get_id(){
		return m_id;
	}
	
    public int getRobustness(){
        return m_robustness;
    }
    
	public boolean contain(Task t){
		return m_tasks.contains(t);
	}
	
	public String toString(){
		String s="";
		
		s+="id: "+m_id + " [";
		for (int i = 0; i < m_tasks.size(); i++) {
			s+=m_tasks.get(i);
			if (i < m_tasks.size() - 1) {
				s+=" , ";
			}
		}
		s+="]";
		return s;
	}
}
