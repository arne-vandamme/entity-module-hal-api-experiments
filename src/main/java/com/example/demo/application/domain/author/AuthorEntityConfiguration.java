package com.example.demo.application.domain.author;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.registry.EntityFactory;
import com.foreach.across.modules.entity.util.EntityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.*;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.util.Base64Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arne Vandamme
 * @since ${projectVersion}
 */
@Configuration
@RequiredArgsConstructor
public class AuthorEntityConfiguration implements EntityConfigurer
{
	private final AuthorService authorService;

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		EntityQueryExecutor<AuthorResource> queryExecutor = new EntityQueryExecutor<AuthorResource>()
		{
			@Override
			public List<AuthorResource> findAll( EntityQuery query ) {
				return new ArrayList<>( authorService.findAll().getContent() );
			}

			@Override
			public List<AuthorResource> findAll( EntityQuery query, Sort sort ) {
				return new ArrayList<>( authorService.find( new PageRequest( 0, 0, sort ) ).getContent() );
			}

			@Override
			public Page<AuthorResource> findAll( EntityQuery query, Pageable pageable ) {
				PagedResources<AuthorResource> authorResources = authorService.find( pageable );
				return new PageImpl<>( EntityUtils.asList( authorResources.getContent() ), pageable,
				                       authorResources.getMetadata().getTotalElements() );
			}
		};

		entities.create()
		        .name( "author" )
		        .as( AuthorResource.class )
		        .entityType( AuthorResource.class, true )
		        .show()
		        .attribute( EntityQueryExecutor.class, queryExecutor )
		        .properties(
				        props -> props.property( "content.name" ).attribute( Sort.Order.class, new Sort.Order( "name" ) )
		        )
		        .listView( lvb -> lvb.showProperties( "content.*" ) )
		        .createFormView( fvb -> fvb.showProperties( "content.*" ) )
		        .updateFormView( fvb -> fvb.showProperties( "content.*" ) )
		        .deleteFormView()
		        .entityModel(
				        model -> model.entityInformation( new EntityInformation<AuthorResource, String>()
				        {
					        @Override
					        public boolean isNew( AuthorResource entity ) {
						        return getId( entity ) != null;
					        }

					        @Override
					        public String getId( AuthorResource entity ) {
						        Link id = entity.getId();
						        return id != null ? Base64Utils.encodeToString( id.getHref().getBytes() ) : null;
					        }

					        @Override
					        public Class<String> getIdType() {
						        return String.class;
					        }

					        @Override
					        public Class<AuthorResource> getJavaType() {
						        return AuthorResource.class;
					        }
				        } )
				                      .findOneMethod( authorService::getById )
				                      .labelPrinter( ( resource, locale ) -> resource.getContent().getName() )
				                      .entityFactory( new EntityFactory<AuthorResource>()
				                      {
					                      @Override
					                      public AuthorResource createNew( Object... objects ) {
						                      return new AuthorResource();
					                      }

					                      @Override
					                      public AuthorResource createDto( AuthorResource author ) {
						                      return new AuthorResource( author.getContent(), author.getLinks() );
					                      }
				                      } )
				                      .saveMethod( authorService::save )
				                      .deleteMethod( authorService::delete )
		        );
	}

}
