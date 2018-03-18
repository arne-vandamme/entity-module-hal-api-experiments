package com.example.demo.application.domain.book;

import com.example.demo.application.domain.author.Author;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
@Data
public class Book
{
	private final Map<String, Object> embedded = new HashMap<String, Object>();

	@NotBlank
	private String title;

	public Book() {
		embedded.put( "author", new Author() );
	}

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@JsonProperty("_embedded")
	public Map<String, Object> getEmbeddedResources() {
		return embedded;
	}
}
