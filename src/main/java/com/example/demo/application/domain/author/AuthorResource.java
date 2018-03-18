package com.example.demo.application.domain.author;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

/**
 * @author Arne Vandamme
 * @since ${projectVersion}
 */
public class AuthorResource extends Resource<Author>
{
	public AuthorResource() {
		super( new Author() );
	}

	public AuthorResource( Author content, Link... links ) {
		super( content, links );
	}

	public AuthorResource( Author content, Iterable<Link> links ) {
		super( content, links );
	}
}
