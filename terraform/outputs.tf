output "gke_cluster_name" {
  value = google_container_cluster.portfolio.name
}

output "gke_cluster_endpoint" {
  value     = google_container_cluster.portfolio.endpoint
  sensitive = true
}

output "artifact_registry_url" {
  value = "${var.region}-docker.pkg.dev/${var.project_id}/${google_artifact_registry_repository.portfolio.repository_id}"
}

output "db_connection_name" {
  value = google_sql_database_instance.postgres.connection_name
}

output "db_private_ip" {
  value     = google_sql_database_instance.postgres.private_ip_address
  sensitive = true
}

output "cicd_service_account" {
  value = google_service_account.cicd.email
}
