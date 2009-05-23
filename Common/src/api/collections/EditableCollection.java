package api.collections;

import api.tracks.Track;

public interface EditableCollection<T extends Track> extends Collection<T> {

	public void setName(String name);
	public void setRoot(boolean root);
	public void setParentId(int parentId);
	public void setSize(int size);
	public void setEditStatus(int editStatus);
	
}
