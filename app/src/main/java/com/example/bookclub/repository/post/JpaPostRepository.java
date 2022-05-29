package com.example.bookclub.repository.post;

import com.example.bookclub.domain.Post;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaPostRepository extends ElasticsearchRepository<Post, String> {
	@Query("{ \"bool\" : { \"must\" : [ { \"query_string\" : { \"query\" : \"*?0*\", \"fields\" : [ \"content\" ] } } ] } }")
	List<Post> findByContent(String content);

	Post save(Post post);
}
