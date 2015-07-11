package xizz.townsquarechallenge.object;

import java.io.Serializable;

public abstract class ArticleContent implements Serializable {
	public static final String TEXT = "singlePostText";
	public static final String IMAGE = "singlePostImage";
	public static final String VIDEO = "singlePostOembed";
	public static final String GALLERY = "singlePostGallery";
	public String type;
}
