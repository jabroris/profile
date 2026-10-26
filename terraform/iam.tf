# ---- GKE node pool service account ----
resource "google_service_account" "gke_nodes" {
  account_id   = "portfolio-gke-nodes"
  display_name = "Portfolio GKE Node Pool SA"
  project      = var.project_id
}

resource "google_project_iam_member" "gke_nodes_log_writer" {
  project = var.project_id
  role    = "roles/logging.logWriter"
  member  = "serviceAccount:${google_service_account.gke_nodes.email}"
}

resource "google_project_iam_member" "gke_nodes_metric_writer" {
  project = var.project_id
  role    = "roles/monitoring.metricWriter"
  member  = "serviceAccount:${google_service_account.gke_nodes.email}"
}

resource "google_artifact_registry_repository_iam_member" "gke_nodes_pull" {
  location   = var.region
  repository = google_artifact_registry_repository.portfolio.name
  role       = "roles/artifactregistry.reader"
  member     = "serviceAccount:${google_service_account.gke_nodes.email}"
}

# ---- Application workload service account (Workload Identity) ----
resource "google_service_account" "app" {
  account_id   = "portfolio-app-sa"
  display_name = "Portfolio App Workload SA"
  project      = var.project_id
}

# Allow Cloud SQL access
resource "google_project_iam_member" "app_cloudsql_client" {
  project = var.project_id
  role    = "roles/cloudsql.client"
  member  = "serviceAccount:${google_service_account.app.email}"
}

# Allow reading secrets
resource "google_project_iam_member" "app_secret_accessor" {
  project = var.project_id
  role    = "roles/secretmanager.secretAccessor"
  member  = "serviceAccount:${google_service_account.app.email}"
}

# Workload Identity binding: k8s SA → GCP SA
resource "google_service_account_iam_member" "workload_identity_binding" {
  service_account_id = google_service_account.app.name
  role               = "roles/iam.workloadIdentityUser"
  member             = "serviceAccount:${var.project_id}.svc.id.goog[portfolio/portfolio-sa]"
}

# ---- CI/CD service account for GitHub Actions ----
resource "google_service_account" "cicd" {
  account_id   = "portfolio-cicd"
  display_name = "Portfolio CI/CD SA (GitHub Actions)"
  project      = var.project_id
}

resource "google_project_iam_member" "cicd_artifact_push" {
  project = var.project_id
  role    = "roles/artifactregistry.writer"
  member  = "serviceAccount:${google_service_account.cicd.email}"
}

resource "google_project_iam_member" "cicd_gke_developer" {
  project = var.project_id
  role    = "roles/container.developer"
  member  = "serviceAccount:${google_service_account.cicd.email}"
}
