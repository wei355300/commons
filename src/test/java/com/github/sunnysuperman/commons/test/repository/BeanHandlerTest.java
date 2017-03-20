package com.github.sunnysuperman.commons.test.repository;

import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import com.github.sunnysuperman.commons.repository.db.BeanHandler;
import com.github.sunnysuperman.commons.repository.db.JdbcTemplate;
import com.github.sunnysuperman.commons.test.BaseTest.TestDB;
import com.github.sunnysuperman.commons.utils.JSONUtil;

public class BeanHandlerTest extends TestCase {

	public static class Image {
		private String url;
		private int w;
		private int h;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public int getW() {
			return w;
		}

		public void setW(int w) {
			this.w = w;
		}

		public int getH() {
			return h;
		}

		public void setH(int h) {
			this.h = h;
		}

	}

	public static class Post {
		private String id;
		private Date createdAt;
		private List<Image> images;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public Date getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(Date createdAt) {
			this.createdAt = createdAt;
		}

		public List<Image> getImages() {
			return images;
		}

		public void setImages(List<Image> images) {
			this.images = images;
		}

	}

	public void test_1() throws Exception {
		BeanHandler<Post> handler = new BeanHandler<Post>(Post.class);
		JdbcTemplate template = TestDB.getJdbcTemplate();
		List<Post> posts = template.findList("select * from post where images is not null", null, 0, 10, handler);
		System.out.println(JSONUtil.stringify(posts));
	}
}
