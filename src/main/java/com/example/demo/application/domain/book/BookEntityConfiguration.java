package com.example.demo.application.domain.book;

import com.example.demo.application.domain.author.AuthorResource;
import com.example.demo.application.domain.author.AuthorService;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.EntityFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.util.Base64Utils;

/**
 * @author Arne Vandamme
 * @since ${projectVersion}
 */
@Configuration
@RequiredArgsConstructor
public class BookEntityConfiguration implements EntityConfigurer
{
	private final BookService bookService;
	private final AuthorService authorService;

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.create()
		        .name( "book" )
		        .as( Resource.class )
		        .entityType( Resource.class, false )
		        .properties(
				        props -> props.property( "content" ).propertyType( Book.class ).and()
				                      .property( "author" )
				                      .displayName( "Author" )
				                      .propertyType( AuthorResource.class )
				                      .hidden( false )
				                      .writable( true )
				                      .readable( true )
						        .<Resource>valueFetcher( resource -> {
							        Link authorLink = resource.getLink( "author" );
							        if ( authorLink != null ) {
								        return authorService.get( authorLink.getHref() );
							        }
							        return null;
						        } )
		        )
		        .show()
		        .listView( lvb -> lvb.pageFetcher( pageable -> bookService.findAll() ).showProperties( "content.*" ) )
		        .createFormView( fvb -> fvb.showProperties( "content.*", "author" ) )
		        .updateFormView( fvb -> fvb.showProperties( "content.*", "author" ) )
		        .deleteFormView()
		        .entityModel(
				        model -> model.entityInformation( new EntityInformation<Resource, String>()
				        {
					        @Override
					        public boolean isNew( Resource entity ) {
						        return getId( entity ) != null;
					        }

					        @Override
					        public String getId( Resource entity ) {
						        Link id = entity.getId();
						        return id != null ? Base64Utils.encodeToString( id.getHref().getBytes() ) : null;
					        }

					        @Override
					        public Class<String> getIdType() {
						        return String.class;
					        }

					        @Override
					        public Class<Resource> getJavaType() {
						        return Resource.class;
					        }
				        } )
				                      .findOneMethod( bookService::getById )
				                      .labelPrinter( ( resource, locale ) -> ( (Book) resource.getContent() ).getTitle() )
				                      .entityFactory( new EntityFactory<Resource>()
				                      {
					                      @Override
					                      public Resource createNew( Object... objects ) {
						                      return new Resource<>( new Book() );
					                      }

					                      @Override
					                      public Resource createDto( Resource book ) {
						                      Resource<Book> clone = new Resource<>( ( (Resource<Book>) book ).getContent() );
						                      clone.add( book.getLinks() );

						                      return clone;
					                      }
				                      } )
				                      .saveMethod( r -> bookService.save( (Resource<Book>) r ) )
				                      .deleteMethod( r -> bookService.delete( (Resource<Book>) r ) )
		        )
		;
	}
}
