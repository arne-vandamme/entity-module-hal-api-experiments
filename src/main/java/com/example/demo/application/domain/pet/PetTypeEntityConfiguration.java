package com.example.demo.application.domain.pet;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.EntityFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.core.support.PersistableEntityInformation;

/**
 * @author Arne Vandamme
 * @since ${projectVersion}
 */
@Configuration
@RequiredArgsConstructor
public class PetTypeEntityConfiguration implements EntityConfigurer
{
	private final PetTypeService petTypeService;

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.create()
		        .as( PetType.class )
		        .entityType( PetType.class, true )
		        .entityModel(
				        model -> model.entityInformation( new PersistableEntityInformation<>( PetType.class ) )
				                      .findOneMethod( petTypeService::getById )
				                      .labelPrinter( ( petType, locale ) -> petType.getName() )
				                      .entityFactory( new EntityFactory<PetType>()
				                      {
					                      @Override
					                      public PetType createNew( Object... objects ) {
						                      return new PetType();
					                      }

					                      @Override
					                      public PetType createDto( PetType petType ) {
						                      return petType.toBuilder().build();
					                      }
				                      } )
				                      .saveMethod( petTypeService::save )
		        )
		        .show()
		        .listView( lvb -> lvb.pageFetcher( pageable -> petTypeService.findAll() ) )
		        .createFormView()
		        .updateFormView()
		;
	}
}
