package it.prova.gestionetratte.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.prova.gestionetratte.model.Airbus;
import it.prova.gestionetratte.repository.airbus.AirbusRepository;
import it.prova.gestionetratte.web.api.exception.AirbusConTratteException;
import it.prova.gestionetratte.web.api.exception.AirbusNotFoundException;

@Service
public class AirbusServiceImpl implements AirbusService {

	@Autowired
	private AirbusRepository repository;

	@Override
	@Transactional(readOnly = true)
	public List<Airbus> listAllElements() {
		return (List<Airbus>) repository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Airbus> listAllElementsEager() {
		return (List<Airbus>) repository.findAllEager();
	}

	@Override
	@Transactional(readOnly = true)
	public Airbus caricaSingoloElemento(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Airbus caricaSingoloElementoConTratte(Long id) {
		return repository.findByIdEager(id);
	}

	@Override
	@Transactional
	public Airbus aggiorna(Airbus airbusInstance) {
		return repository.save(airbusInstance);
	}

	@Override
	@Transactional
	public Airbus inserisciNuovo(Airbus airbusInstance) {
		return repository.save(airbusInstance);
	}

	@Override
	@Transactional
	public void rimuovi(Airbus airbusInstance) {
		repository.delete(airbusInstance);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Airbus> findByExampleWithPagination(Airbus example, Integer pageNo, Integer pageSize, String sortBy) {
		Specification<Airbus> specificationCriteria = (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<Predicate>();

			if (StringUtils.isNotEmpty(example.getCodice()))
				predicates.add(cb.like(cb.upper(root.get("codice")), "%" + example.getCodice().toUpperCase() + "%"));

			if (StringUtils.isNotEmpty(example.getDescrizione()))
				predicates.add(
						cb.like(cb.upper(root.get("descrizione")), "%" + example.getDescrizione().toUpperCase() + "%"));

			if (example.getNumeroPasseggeri() != null)
				predicates.add(cb.greaterThanOrEqualTo(root.get("numeroPasseggeri"), example.getNumeroPasseggeri()));

			if (example.getDataInizioServizio() != null)
				predicates
						.add(cb.greaterThanOrEqualTo(root.get("dataInizioServizio"), example.getDataInizioServizio()));

			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};

		Pageable paging = null;
		// se non passo parametri di paginazione non ne tengo conto
		if (pageSize == null || pageSize < 10)
			paging = Pageable.unpaged();
		else
			paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

		return repository.findAll(specificationCriteria, paging);
	}

	@Override
	@Transactional
	public void rimuovi(Long idAirbusToRemove) {
		Airbus toRemove = repository.findByIdEager(idAirbusToRemove);
		if (toRemove == null) {
			throw new AirbusNotFoundException("Airbus con id: " + idAirbusToRemove + " non trovato");
		}
		if (toRemove.getTratte() != null && toRemove.getTratte().size() > 0) {
			throw new AirbusConTratteException("L'airbus con id: " + idAirbusToRemove + " ha ancora tratte associate");
		}
		repository.deleteById(idAirbusToRemove);
	}

}
