package it.prova.gestionetratte.repository.tratta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import it.prova.gestionetratte.model.Tratta;

public interface TrattaRepository extends PagingAndSortingRepository<Tratta, Long>, JpaSpecificationExecutor<Tratta> {
	@Query("from Tratta t join fetch t.airbus where t.id = ?1")
	Tratta findSingleTrattaEager(Long id);

	@Query("select t from Tratta t join fetch t.airbus")
	List<Tratta> findAllTrattaEager();
}
