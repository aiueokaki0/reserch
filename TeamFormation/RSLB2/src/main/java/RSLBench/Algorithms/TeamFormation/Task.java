package RSLBench.Algorithms.TeamFormation;

public class Task {
	
	private int m_id;
	
	public Task (){
		
	}
	
	public Task (int id){
		m_id = id;
	}
	
	public int get_id(){
		return m_id;
	}
	
	public void set_id(int id){
		m_id = id;
	}
	
	public boolean equals(Task obj) {
		return m_id == obj.get_id();
	}
	@Override
	public String toString() {
		String s;
		
		s = "" + m_id;
		return s;
	}
	
	
}
