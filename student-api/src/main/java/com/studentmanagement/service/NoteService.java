package com.studentmanagement.service;

import com.studentmanagement.dto.NoteDTO;
import com.studentmanagement.dto.StudentAverageDTO;
import com.studentmanagement.dto.ClassStatisticsDTO;
import com.studentmanagement.model.Etudiant;
import com.studentmanagement.model.Matiere;
import com.studentmanagement.model.Note;
import com.studentmanagement.model.Niveau;
import com.studentmanagement.repository.EtudiantRepository;
import com.studentmanagement.repository.MatiereRepository;
import com.studentmanagement.repository.NoteRepository;
import com.studentmanagement.repository.NiveauRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private NiveauRepository niveauRepository;

    @Autowired
    private MatiereRepository matiereRepository;

    private NoteDTO mapToDTO(Note note) {
        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        dto.setEtudiantId(note.getEtudiant().getId());
        dto.setMatiereId(note.getMatiere().getId());
        dto.setValeur(note.getValeur());
        dto.setSemestre(note.getSemestre());
        dto.setAnnee(note.getAnnee());
        return dto;
    }

    public NoteDTO addNote(String etudiantId, String matiereId, float valeur, String semestre, String annee) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new IllegalArgumentException("Étudiant non trouvé."));
        Matiere matiere = matiereRepository.findById(matiereId)
                .orElseThrow(() -> new IllegalArgumentException("Matière non trouvée."));
        if (valeur < 0 || valeur > 20) {
            throw new IllegalArgumentException("La note doit être entre 0 et 20.");
        }
        Note note = new Note();
        note.setEtudiant(etudiant);
        note.setMatiere(matiere);
        note.setValeur(valeur);
        note.setSemestre(semestre);
        note.setAnnee(annee);
        return mapToDTO(noteRepository.save(note));
    }

    public List<NoteDTO> getNotesByEtudiant(String etudiantId) {
        return noteRepository.findByEtudiantId(etudiantId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<NoteDTO> getNotesByMatiere(String matiereId) {
        return noteRepository.findByMatiereId(matiereId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<NoteDTO> getNoteById(String id) {
        return noteRepository.findById(id)
                .map(this::mapToDTO);
    }

    public NoteDTO updateNote(String id, String etudiantId, String matiereId, float valeur, String semestre, String annee) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note non trouvée."));
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new IllegalArgumentException("Étudiant non trouvé."));
        Matiere matiere = matiereRepository.findById(matiereId)
                .orElseThrow(() -> new IllegalArgumentException("Matière non trouvée."));
        if (valeur < 0 || valeur > 20) {
            throw new IllegalArgumentException("La note doit être entre 0 et 20.");
        }
        note.setEtudiant(etudiant);
        note.setMatiere(matiere);
        note.setValeur(valeur);
        note.setSemestre(semestre);
        note.setAnnee(annee);
        return mapToDTO(noteRepository.save(note));
    }

    public void deleteNote(String id) {
        if (!noteRepository.existsById(id)) {
            throw new IllegalArgumentException("Note non trouvée.");
        }
        noteRepository.deleteById(id);
    }

    public List<NoteDTO> getAllNotes(String semestre, String annee) {
        if (semestre != null && annee != null) {
            return noteRepository.findBySemestreAndAnnee(semestre, annee)
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } else if (semestre != null) {
            return noteRepository.findBySemestre(semestre)
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } else if (annee != null) {
            return noteRepository.findByAnnee(annee)
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } else {
            return noteRepository.findAll()
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        }
    }

    public List<StudentAverageDTO> getStudentAverages(String semestre, String annee) {
        List<Etudiant> students = etudiantRepository.findAll();
        return students.stream()
                .map(student -> calculateStudentAverage(student, semestre, annee))
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    private StudentAverageDTO calculateStudentAverage(Etudiant student, String semestre, String annee) {
        List<Note> notes;
        if (semestre != null && annee != null) {
            notes = noteRepository.findByEtudiantIdAndSemestreAndAnnee(student.getId(), semestre, annee);
        } else if (semestre != null) {
            notes = noteRepository.findByEtudiantIdAndSemestre(student.getId(), semestre);
        } else if (annee != null) {
            notes = noteRepository.findByEtudiantIdAndAnnee(student.getId(), annee);
        } else {
            notes = noteRepository.findByEtudiantId(student.getId());
        }

        if (notes.isEmpty()) {
            return null;
        }

        double weightedSum = 0;
        int totalCoefficient = 0;
        for (Note note : notes) {
            int coefficient = note.getMatiere().getCoefficient();
            weightedSum += note.getValeur() * coefficient;
            totalCoefficient += coefficient;
        }

        double moyenne = totalCoefficient > 0 ? weightedSum / totalCoefficient : 0;
        StudentAverageDTO dto = new StudentAverageDTO();
        dto.setEtudiantId(student.getId());
        dto.setMatricule(student.getMatricule());
        dto.setPrenom(student.getPrenom());
        dto.setNom(student.getNom());
        dto.setMoyenne(Math.round(moyenne * 100.0) / 100.0);
        dto.setAdmissionStatus(moyenne >= 10 ? "Admis" : "Non Admis");
        dto.setMention(getMention(moyenne));
        return dto;
    }

    public ClassStatisticsDTO getClassStatistics(String niveauId, String semestre, String annee, int topN) {
        Niveau niveau = niveauRepository.findById(niveauId)
                .orElseThrow(() -> new IllegalArgumentException("Niveau non trouvé."));
        List<Etudiant> students = etudiantRepository.findByNiveauId(niveauId);
        List<StudentAverageDTO> studentAverages = students.stream()
                .map(student -> calculateStudentAverage(student, semestre, annee))
                .filter(dto -> dto != null)
                .sorted((a, b) -> Double.compare(b.getMoyenne(), a.getMoyenne()))
                .collect(Collectors.toList());

        if (studentAverages.isEmpty()) {
            throw new IllegalArgumentException("Aucune note trouvée pour ce niveau.");
        }

        double moyenneGenerale = studentAverages.stream()
                .mapToDouble(StudentAverageDTO::getMoyenne)
                .average()
                .orElse(0.0);
        double maxMoyenne = studentAverages.stream()
                .mapToDouble(StudentAverageDTO::getMoyenne)
                .max()
                .orElse(0.0);
        double minMoyenne = studentAverages.stream()
                .mapToDouble(StudentAverageDTO::getMoyenne)
                .min()
                .orElse(0.0);
        List<StudentAverageDTO> topStudents = studentAverages.stream()
                .limit(topN)
                .collect(Collectors.toList());

        ClassStatisticsDTO stats = new ClassStatisticsDTO();
        stats.setNiveauId(niveauId);
        stats.setNiveauNom(niveau.getNom());
        stats.setMoyenneGenerale(Math.round(moyenneGenerale * 100.0) / 100.0);
        stats.setMaxMoyenne(maxMoyenne);
        stats.setMinMoyenne(minMoyenne);
        stats.setTopStudents(topStudents);
        stats.setAllStudents(studentAverages);
        return stats;
    }

    private String getMention(double moyenne) {
        if (moyenne >= 16) {
            return "Très Bien";
        } else if (moyenne >= 14) {
            return "Bien";
        } else if (moyenne >= 12) {
            return "Assez Bien";
        } else if (moyenne >= 10) {
            return "Passable";
        } else {
            return "Aucune";
        }
    }
}