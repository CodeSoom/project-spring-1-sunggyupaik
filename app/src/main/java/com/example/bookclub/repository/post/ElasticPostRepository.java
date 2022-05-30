package com.example.bookclub.repository.post;

import com.example.bookclub.domain.Post;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ElasticPostRepository extends ElasticsearchRepository<Post, String> {
	@Query("{ \"bool\" : { \"must\" : [ { \"query_string\" : { \"query\" : \"*?0*\", \"fields\" : [ \"content\" ] } } ] } }")
	List<Post> findByContent(String content);

	Post save(Post post);

	@Query("{ \"query\" : { \"match_all\" : {} }, \"sort\": { \"createdDate\": \"asc\" } }")
	List<Post> findAll();
}
