package pl.mstudio.mplayer;

public class TaskSong {
	private String uri;
    private String name;
    private String album;
    private int album_id;

	public TaskSong(String name, String uri, String album, int albumId) {
        this.name = name;
        this.uri = uri;
        this.album = album;
        this.album_id = albumId;
    }

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
    public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}
	
	public int getAlbum_id() {
		return album_id;
	}

	public void setAlbum_id(int album_id) {
		this.album_id = album_id;
	}

}
