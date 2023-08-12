// GitLab API Endpoint and Project ID
const apiUrl = 'https://gitlab.w-hs.de/api/v4/projects/:id/repository/commits';
const projectId = '1112';

// Make the API request to retrieve commits
fetch(apiUrl.replace(':id', projectId), {
  headers: {
    'Authorization': `Bearer glpat-gzN2JQncbPFw67NGS1qg`
  }
})
  .then(response => response.json())
  .then(commits => {
    const commitList = document.querySelector('.commit_List');

    commits.forEach(commit => {
      const commitLink = document.createElement('a');
      commitLink.href = commit.web_url;
      commitLink.target = '_blank';
      commitLink.innerText = commit.id.slice(0, 5) + "...";
      commitLink.style.marginLeft = 'auto'; // Push the commit ID to the right side

      const commitDate = document.createElement('span');
      commitDate.innerText = new Date(commit.created_at).toLocaleString();

      const commitMessage = document.createElement('span');
      commitMessage.innerText = commit.message;

      const listItem = document.createElement('li');
      listItem.appendChild(commitDate);
      listItem.appendChild(commitMessage);
      listItem.appendChild(commitLink);

      commitList.appendChild(listItem);
    });
  })
  .catch(error => {
    console.error('Error fetching commits:', error);
  });

// Fetch jobs data
fetch('https://gitlab.w-hs.de/api/v4/projects/1112/jobs', {
  headers: {
    'PRIVATE-TOKEN': 'glpat-gzN2JQncbPFw67NGS1qg'
  }
})
  .then(response => response.json())
  .then(jobs => {
    console.log(jobs);
    console.log(jobs[0].id);
    const archiveUrl = "https://gitlab.w-hs.de/api/v4/projects/1112/jobs/" + jobs[0].id + "/artifacts/";

    const button = document.querySelector(".arcive_download");
    button.href = archiveUrl;
    button.target = '_blank';
    console.log(archiveUrl);
  });

// Make the API request to fetch pipelines
fetch('https://gitlab.w-hs.de/api/v4/projects/1112/pipelines', {
  headers: {
    'PRIVATE-TOKEN': 'glpat-gzN2JQncbPFw67NGS1qg'
  }
})
  .then(response => response.json())
  .then(pipelines => {
    if (pipelines.length > 0) {
      const lastPipeline = pipelines[0];
      const lastRunStatus = lastPipeline.status;
      const lastPipelineUrl = lastPipeline.web_url;

      // Update the HTML with the last run status and link
      const lastRunStatusElement = document.querySelector('.lastRunStatus');

      const lastPipelineLink = document.createElement('a');
      lastPipelineLink.href = lastPipelineUrl;
      lastPipelineLink.innerText = lastRunStatus;
      lastPipelineLink.target = '_blank';

      if (lastRunStatus === 'failure') {
        lastPipelineLink.style.color = 'red';

      } else if (lastRunStatus === 'success') {
        lastPipelineLink.style.color = 'green';
      } else if (lastRunStatus === 'running')

        lastPipelineLink.style.color = 'yellow';
      }

      lastPipelineLink.style.textDecoration = 'none';
      lastRunStatusElement.appendChild(lastPipelineLink);
    } else {
      const lastRunStatusElement = document.querySelector('.lastRunStatus');
      lastRunStatusElement.innerText = 'No pipelines found';
    }
  })
  .catch(error => {
   console.error('Error fetching pipelines:', error);
      const lastRunStatusElement = document.querySelector('.lastRunStatus');
      lastRunStatusElement.innerText = 'Error fetching pipelines';
    });