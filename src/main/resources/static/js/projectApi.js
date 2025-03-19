let clientProjectCache = {
  projects: [],
  projectTypes: [],
};

const simulateProjectUpdate = (project) => {
  clientProjectCache.projects = [...clientProjectCache.projects, project];
  renderProjectList();
};

const simulateProjectTypesUpdate = (projectTypes) => {
  clientProjectCache.projectTypes = projectTypes;
  renderProjectTypesOptions();
}

// Fetch initial data and populate cache
const initializeData = async () => {
  try {
    clientProjectCache.projects = await fetchData("/projects/");
    clientProjectCache.projectTypes = await fetchData("/projects/types"); // Fetch project types
    renderProjectList();
    renderProjectTypesOptions();
  } catch (error) {
    renderError(error.message);
  }
};

const renderProjectTypesOptions = () => {
  const projectDropdowns = document.getElementsByClassName(
    "project-type-dropdown"
  );
  Array.from(projectDropdowns).forEach((dropdown) => {
    dropdown.innerHTML = "";
    clientProjectCache.projectTypes.forEach((type) => {
      const html = document.createElement("option");
      html.setAttribute("value", type);
      html.innerHTML = type;
      dropdown.appendChild(html);
    });
  });
};

const renderProjectList = () => {
  const projectList = document.getElementById("projectList");
  projectList.innerHTML = "";
  clientProjectCache.projects.forEach((project) => {
    const projectHtml = document.createElement("p");
    projectHtml.innerHTML = `
            <b>ID :</b>${project.id}</br>
            <b>NAME :</b>${project.name}</br>
            <b>TYPE :</b>${project.type}</br>
        `;
    projectList.appendChild(projectHtml);
  });
};


const fetchData = async (url, options = {}) => {
  try {
    const res = await fetch(url, options);
    if (!res.ok) {
      const errorData = await res.json();
      throw new Error(errorData.message || `HTTP error! Status: ${res.status}`);
    }
    return await res.json();
  } catch (error) {
    console.error("Fetch error:", error);
    throw error;
  }
};

const addNewProject = async () => {
  const form = document.getElementById("addNewProject").elements;
  const name = form["name"].value;
  const type = form["type"].value;
  const project = {
    name,
    type
  }
  try {
    const newProject = await fetchData("/projects/", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(project) });
    alert(`New Project added with id ${newProject.id}:(${JSON.stringify(newProject)})`);
    simulateProjectUpdate(newProject);
  } catch (error) {
    renderError(error.message);
  }
}

const renderError = (message) => {
  alert(`Error calling API: ${message}`);
}

initializeData();