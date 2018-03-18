package com.example.demo.application.domain;

import com.example.demo.application.domain.author.AuthorResource;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;

/**
 * @author Arne Vandamme
 * @since ${projectVersion}
 */
public class HypermediaApiConnector<T extends Resource<?>>
{
	@Setter
	private RestTemplate apiClient;

	private final String path;
	private final Class<T> resourceType;

	private final ParameterizedTypeReference<PagedResources<T>> pagedResultsRef;

	public HypermediaApiConnector( String path, Class<T> resourceType ) {
		this.path = path;
		this.resourceType = resourceType;

		pagedResultsRef = new ParameterizedTypeReference<PagedResources<T>>()
		{
			@Override
			public Type getType() {
				return new ParameterizedType()
				{
					@Override
					public Type[] getActualTypeArguments() {
						return new Type[] { AuthorResource.class };
					}

					@Override
					public Type getRawType() {
						return PagedResources.class;
					}

					@Override
					public Type getOwnerType() {
						return PagedResources.class;
					}
				};
			}
		};
	}

	public PagedResources<T> findAll() {
		return apiClient.exchange( path, HttpMethod.GET, null, pagedResultsRef ).getBody();
	}

	public PagedResources<T> find( Pageable pageable ) {
		return apiClient.exchange( path + "?" + queryStringFromPageable( pageable ), HttpMethod.GET, null, pagedResultsRef ).getBody();
	}

	@SneakyThrows
	private String queryStringFromPageable( Pageable p ) {
		StringBuilder ans = new StringBuilder();
		ans.append( "size=" );
		ans.append( p.getPageSize() );
		ans.append( "&page=" );
		ans.append( URLEncoder.encode( p.getPageNumber() + "", "UTF-8" ) );

		// No sorting
		if ( p.getSort() == null ) {
			return ans.toString();
		}

		// Sorting is specified
		for ( Sort.Order o : p.getSort() ) {
			ans.append( "&sort=" );
			ans.append( URLEncoder.encode( o.getProperty(), "UTF-8" ) );
			ans.append( "," );
			ans.append( URLEncoder.encode( o.getDirection().name(), "UTF-8" ) );
		}

		return ans.toString();
	}

	public T getById( Serializable id ) {
		return get( new String( Base64Utils.decodeFromString( id.toString() ) ) );
	}

	public T get( String url ) {
		return apiClient.exchange( url, HttpMethod.GET, null, resourceType ).getBody();
	}

	public T save( T bookResource ) {
		HttpEntity<?> postData = new HttpEntity<>( bookResource.getContent() );

		if ( bookResource.getId() == null ) {
			return apiClient.exchange( path, HttpMethod.POST, postData, resourceType ).getBody();
		}

		ResponseEntity<T> response = apiClient.exchange( bookResource.getId().getHref(), HttpMethod.PUT, postData, resourceType );
		return response.getBody();
	}

	public void delete( T bookResource ) {
		apiClient.delete( bookResource.getId().getHref() );
	}
}
