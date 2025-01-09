package gr.uom.strategicplanning.services.euproject;

import gr.uom.strategicplanning.controllers.entities.ProjectSlim;
import gr.uom.strategicplanning.models.euproject.Organization;
import gr.uom.strategicplanning.models.euproject.Project;
import gr.uom.strategicplanning.repositories.euproject.OrganizationRepository;
import gr.uom.strategicplanning.repositories.euproject.ProjectRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.io.*;
import java.util.*;

@Service
public class OrganizationService {

    @Autowired
    OrganizationRepository organizationRepository;
    @Autowired
    ProjectRepository projectRepository;

    @Transactional
    public List<Organization> getAllOrganization(){
        return organizationRepository.findAll();
    }

    @Transactional
    public Organization getOrganizationWithName(String name){
        Organization organization = organizationRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization with name "+name+" doesn't exist");
                });
        return organization;
    }

    @Transactional
    public void addOrganizations() throws IOException {
        String[] HEADERS = { "projectRcn","projectID","projectAcronym","role","id","name","shortName","activityType",
                "endOfParticipation","ecContribution","country","street","city","postCode","organizationUrl","vatNumber",
                "contactForm","contactType","contactTitle","contactFirstNames","contactLastNames","contactFunction",
                "contactTelephoneNumber","contactFaxNumber" };
        InputStream stream= ProjectService.class.getResourceAsStream("/cordis-h2020organizations.csv");
        Reader in = new InputStreamReader(stream);

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setDelimiter(";")
                .setHeader(HEADERS)
                .setSkipHeaderRecord(true)
                .build();

        Iterable<CSVRecord> records = csvFormat.parse(in);

        //for each record in the csv
        for (CSVRecord record : records) {
            Organization organizationOptional = organizationRepository.findById(Long.parseLong(record.get("id"))).orElse(null);
            Project projectOptional = projectRepository.findById(Long.parseLong(record.get("projectID"))).orElse(null);

            //if the organization does not exist
            if (organizationOptional == null) {
                Organization organization = new Organization(Long.parseLong(record.get("id")),record.get("name"),
                        record.get("shortName"),record.get("country"),record.get("street"),record.get("city"),
                        record.get("postCode"),record.get("organizationUrl"),record.get("vatNumber"));

                if(projectOptional != null){
                    if(record.get("role").equals("coordinator")){
                        projectOptional.setCoordinator(organization);
                        organization.addCoordination(projectOptional);
                    }
                    else if (record.get("role").equals("partner") || record.get("role").equals("participant")){
                        projectOptional.addParticipants(organization);
                        organization.addProjects(projectOptional);
                    }
                }
                else {
                    System.err.println("projectID: " + organization.getId() );
                }

                organizationRepository.save(organization);
            }
            else {
                if(projectOptional != null){
                    if(record.get("role").equals("coordinator")){
                        projectOptional.setCoordinator(organizationOptional);
                        organizationOptional.addCoordination(projectOptional);
                    }
                    else if (record.get("role").equals("partner") || record.get("role").equals("participant")){
                        projectOptional.addParticipants(organizationOptional);
                        organizationOptional.addProjects(projectOptional);
                    }
                }
                else {
                    System.err.println("projectID: " + record.get("id") );
                }
            }

        }
    }

    @Transactional
    public List<ProjectSlim> getProjectsCoordinatedByOrg(String orgId) {
        Organization organization = organizationRepository.findById(Long.parseLong(orgId))
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization with id "+orgId+" doesn't exist");
                });

        List<ProjectSlim> coordinatingSlimProjects = new ArrayList<>();
        for (Project pr:organization.getCoordinations()){
            coordinatingSlimProjects.add(new ProjectSlim(pr.getId(),pr.getAcronym(),pr.getTitle(),pr.getStartDate(),pr.getEndDate(),
                    pr.getObjective(),pr.getTotalCost(),pr.getProgramme(),pr.getTopics(),pr.getFrameworkProgramme(),pr.getCall(),
                    pr.getFundingScheme()));
        }

        return coordinatingSlimProjects;
    }

    @Transactional
    public List<ProjectSlim> getProjectsParticipatedOrg(String orgId) {
        Organization organization = organizationRepository.findById(Long.parseLong(orgId))
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization with id "+orgId+" doesn't exist");
                });

        List<ProjectSlim> coordinatingSlimProjects = new ArrayList<>();
        for (Project pr:organization.getProjects()){
            coordinatingSlimProjects.add(new ProjectSlim(pr.getId(),pr.getAcronym(),pr.getTitle(),pr.getStartDate(),pr.getEndDate(),
                    pr.getObjective(),pr.getTotalCost(),pr.getProgramme(),pr.getTopics(),pr.getFrameworkProgramme(),pr.getCall(),
                    pr.getFundingScheme()));
        }

        return coordinatingSlimProjects;
    }

    @Transactional
    public HashMap<Organization, Integer> getTopPartners(String orgId) {
        HashMap<Organization, Integer> partners = new HashMap<>();
        Organization organization = organizationRepository.findById(Long.parseLong(orgId))
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization with id "+orgId+" doesn't exist");
                });

        List<Project> projects = new ArrayList<>(organization.getProjects());
        projects.addAll(organization.getCoordinations());

        for (Project pr:projects){
            List<Organization> organizations = new ArrayList<>(pr.getParticipants());
            organizations.add(pr.getCoordinator());
            organizations.remove(organization);

            for(Organization org: organizations){
                Integer i=partners.get(org);
                partners.put(org, (i==null) ? 1 : i+1);
            }
        }

        HashMap<Organization,Integer> sortedPartners = sortByValue(partners);
        return sortedPartners;
    }



    /**
     * Function to sort hashmap by values
     * @param hm HashMap to be sorted by integer value
     * @return sorted hashmap
     */
    public HashMap<Organization, Integer> sortByValue(HashMap<Organization, Integer> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<Organization, Integer> > list = new LinkedList<>(hm.entrySet());

        // Sort the list
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // put data from sorted list to hashmap
        HashMap<Organization, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<Organization, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }

        return temp;
    }
}
