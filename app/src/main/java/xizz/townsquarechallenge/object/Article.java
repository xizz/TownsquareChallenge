package xizz.townsquarechallenge.object;

import java.io.Serializable;

public class Article implements Serializable {
	public int id;
	public String title;
	public String imageUrl;
	public String date;
	public String url;
	public String jsonUrl;
	public String[] authors;
	public ArticleContent[] contents;
}
