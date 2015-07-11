package xizz.townsquarechallenge.object;

public class ContentVideo extends ArticleContent {
	public String thumbnailUrl;
	public String videoUrl;

	public ContentVideo() {
		this("", "");
	}

	public ContentVideo(String t, String v) {
		thumbnailUrl = t;
		videoUrl = v;
	}
}
