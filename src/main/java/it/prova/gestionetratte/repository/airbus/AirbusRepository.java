package it.prova.gestionetratte.repository.airbus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import it.prova.gestionetratte.model.Airbus;

public interface AirbusRepository extends PagingAndSortingRepository<Airbus, Long>, JpaSpecificationExecutor<Airbus> {

	@Query("select distinct a from Airbus a left join fetch a.tratte ")
	List<Airbus> findAllEager();

	@Query("from Airbus a left join fetch a.tratte where a.id=?1")
	Airbus findByIdEager(Long idTratta);
}
