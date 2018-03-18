package com.example.demo.application.domain.book;

import com.foreach.across.core.annotations.PostRefresh;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.net.URI;

/**
 * @author Arne Vandamme
 * @since ${projectVersion}
 */
@Service
@RequiredArgsConstructor
public class BookService
{
	private RestTemplate bookApiClient;

	@PostRefresh
	public void setBookApiClient( RestTemplate bookApiClient ) {
		this.bookApiClient = bookApiClient;
	}

	private final Traverson traverson = new Traverson( URI.create( "http://localhost:36449" ), MediaTypes.HAL_JSON );

	public PagedResources<Resource<Book>> findAll() {
		ParameterizedTypeReference<PagedResources<Resource<Book>>> booksRef = new ParameterizedTypeReference<PagedResources<Resource<Book>>>()
		{
		};

		return traverson.follow( "books" )
		                .toObject( booksRef );
	}

	/*
	public List<PetType> findAll() {
		return Arrays.asList( petApiClient.getForObject( "/pettypes", PetType[].class ) );
	}
*/
	public Resource<Book> getById( Serializable id ) {
		ParameterizedTypeReference<Resource<Book>> bookRef = new ParameterizedTypeReference<Resource<Book>>()
		{
		};

		return bookApiClient.exchange( new String( Base64Utils.decodeFromString( id.toString() ) ), HttpMethod.GET, null, bookRef ).getBody();
		//return petApiClient.getForObject( "/pettypes/" + id, PetType.class );
	}

	public Resource<Book> save( Resource<Book> bookResource ) {
		ParameterizedTypeReference<Resource<Book>> bookRef = new ParameterizedTypeReference<Resource<Book>>()
		{
		};
		HttpEntity<Book> postData = new HttpEntity<>( bookResource.getContent() );

		if ( bookResource.getId() == null ) {
			return bookApiClient.exchange( "/books", HttpMethod.POST, postData, bookRef ).getBody();
			//return petApiClient.postForObject( "/pettypes", petType, PetType.class );
		}

		ResponseEntity<Resource<Book>> response = bookApiClient.exchange( bookResource.getId().getHref(), HttpMethod.PUT, postData, bookRef );
		return response.getBody();
	}

	public void delete( Resource<Book> bookResource ) {
		bookApiClient.delete( bookResource.getId().getHref() );
	}
	/*
	public PetType save( PetType petType ) {
		if ( petType.isNew() ) {

		}

		HttpEntity<PetType> postData = new HttpEntity<>( petType );
		petApiClient.exchange( "/pettypes/" + petType.getId(), HttpMethod.PUT, postData, Void.class );
		//petApiClient.put(  ).postForEntity( "/pettypes/" + petType.getId(), petType );

		return petType;
	}
	*/
}
