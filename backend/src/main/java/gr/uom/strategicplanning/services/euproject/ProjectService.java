package gr.uom.strategicplanning.services.euproject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import gr.uom.strategicplanning.controllers.entities.CallEuropa;
import gr.uom.strategicplanning.models.euproject.Project;
import gr.uom.strategicplanning.repositories.euproject.OrganizationRepository;
import gr.uom.strategicplanning.repositories.euproject.ProjectRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ProjectService {

    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    OrganizationRepository organizationRepository;

    @Transactional
    public Project getProjectWithTitle(String title){
        Project project = projectRepository.findByTitle(title)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project with title "+title+" doesn't exist");
                });
        return project;
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> getAllProjects(String containTitle, String containObjective, int page, int size){
        Pageable paging = PageRequest.of(page, size);
        Page<Project> pageProject = null;
        if((containTitle==null || containTitle.equals("")) && (containObjective==null || containObjective.equals(""))) {
            pageProject = projectRepository.findAll(paging);
        }
        else if(containTitle!=null && !containTitle.equals("")){
            pageProject = projectRepository.findByTitleContainingIgnoreCase(containTitle, paging);
        }
        else {
            pageProject = projectRepository.findByObjectiveContainingIgnoreCase(containObjective, paging);
        }

        if(pageProject!=null) {
            Map<String, Object> response = new HashMap<>();
            List<Project> projects = pageProject.getContent();
            response.put("projects", projects);
            response.put("currentPage", pageProject.getNumber());
            response.put("totalItems", pageProject.getTotalElements());
            response.put("totalPages", pageProject.getTotalPages());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(new HashMap<>(), HttpStatus.NO_CONTENT);
        }
    }

    @Transactional
    public void addProjects() throws IOException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String[] HEADERS = { "rcn","id","acronym","status","programme","topics","frameworkProgramme","title",
                "startDate","endDate","projectUrl","objective","totalCost","ecMaxContribution","call","fundingScheme",
                "coordinator","coordinatorCountry","participants","participantCountries","subjects" };
        InputStream stream= ProjectService.class.getResourceAsStream("/cordis-h2020projects.csv");
        Reader in = new InputStreamReader(stream);

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setDelimiter(";")
                .setHeader(HEADERS)
                .setSkipHeaderRecord(true)
                .build();

        Iterable<CSVRecord> records = csvFormat.parse(in);

        //for each record in the csv
        for (CSVRecord record : records) {
            Date startDate = null;
            if(!record.get("startDate").isEmpty()) {
                startDate = sdf.parse(record.get("startDate"));
            }
            Date endDate = null;
            if(!record.get("endDate").isEmpty()) {
                endDate = sdf.parse(record.get("endDate"));
            }
            Project project = new Project(Long.parseLong(record.get("id")),record.get("acronym"),record.get("title"),
                    startDate,endDate,record.get("objective"),record.get("totalCost"),record.get("programme"),
                    record.get("topics"),record.get("frameworkProgramme"),record.get("call"),record.get("fundingScheme"));

            projectRepository.save(project);
        }
    }


    public List<CallEuropa> getNewProjects(Boolean open, String type, String sortType) {
        List<CallEuropa> callEuropas = new ArrayList<>();
        String query="";
        if(open && (type==null || type.equals(""))){
            query = "{\"bool\":{\"must\":[{\"terms\":{\"type\":[\"1\",\"2\",\"8\",\"0\"]}},{\"terms\":{\"status\":[\"31094502\"]}}]}}";
        }
        else if (!open && (type==null || type.equals(""))) {
            query = "{\"bool\":{\"must\":[{\"terms\":{\"type\":[\"1\",\"2\",\"8\",\"0\"]}},{\"terms\":{\"status\":[\"31094501\"]}}]}}";
        }
        else if (open && type.equals("Tenders")){
            query = "{\"bool\":{\"must\":[{\"terms\":{\"type\":[\"0\"]}},{\"terms\":{\"status\":[\"31094502\"]}}]}}";
        }
        else if (open && type.equals("Grants")){
            query = "{\"bool\":{\"must\":[{\"terms\":{\"type\":[\"1\",\"2\",\"8\"]}},{\"terms\":{\"status\":[\"31094502\"]}}]}}";
        }
        else if (!open && type.equals("Tenders")){
            query = "{\"bool\":{\"must\":[{\"terms\":{\"type\":[\"0\"]}},{\"terms\":{\"status\":[\"31094501\"]}}]}}";
        }
        else if (!open && type.equals("Grants")){
            query = "{\"bool\":{\"must\":[{\"terms\":{\"type\":[\"1\",\"2\",\"8\"]}},{\"terms\":{\"status\":[\"31094501\"]}}]}}";
        }

        String sort="";
        if (sortType.equals("deadlineDate")){
            sort = "{\"order\":\"ASC\",\"field\":\"deadlineDate\"}";
        }
        else {
            sort = "{\"order\":\"ASC\",\"field\":\"startDate\"}";
        }

        try {
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter("/query.json"));
            outputWriter.write(query);
            outputWriter.close();

            BufferedWriter outputWriter2 = new BufferedWriter(new FileWriter("/sort.json"));
            outputWriter2.write(sort);
            outputWriter2.close();

            Unirest.setTimeouts(0, 0);
            try {
                HttpResponse<String> response = Unirest.post("https://api.tech.ec.europa.eu/search-api/prod/rest/search?apiKey=SEDIA&text=***&pageSize=1000&pageNumber=1")
                        .field("query", new File("/query.json"))
                        .field("sort", new File("/sort.json"))
                        .asString();
                if(response.getStatus()==200){
                    String text = new String(response.getRawBody().readAllBytes(), StandardCharsets.UTF_8);
                    System.out.println(text);
                    //ToDo
                    //parse response
                    // and return Calls
                    return callEuropas;
                }
                else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Response from Europa cannot be found");
                }
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
