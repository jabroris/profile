resource "google_artifact_registry_repository" "portfolio" {
  location      = var.region
  repository_id = "portfolio"
  description   = "Docker images for the portfolio application"
  format        = "DOCKER"
  project       = var.project_id
  depends_on    = [google_project_service.apis]
}
