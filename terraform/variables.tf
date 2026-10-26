variable "project_id" {
  description = "GCP Project ID"
  type        = string
  default     = "profile-123456789"
}

variable "region" {
  description = "GCP region"
  type        = string
  default     = "us-central1"
}

variable "zone" {
  description = "GCP zone for zonal resources"
  type        = string
  default     = "us-central1-a"
}

variable "environment" {
  description = "Environment name (staging or production)"
  type        = string
  default     = "production"
  validation {
    condition     = contains(["staging", "production"], var.environment)
    error_message = "Environment must be 'staging' or 'production'."
  }
}

variable "gke_node_count" {
  description = "Initial nodes per zone in the GKE cluster"
  type        = number
  default     = 2
}

variable "gke_machine_type" {
  description = "GCE machine type for GKE nodes"
  type        = string
  default     = "e2-standard-2"
}

variable "db_tier" {
  description = "Cloud SQL tier"
  type        = string
  default     = "db-g1-small"
}

variable "db_password" {
  description = "Cloud SQL master password (supply via TF_VAR_db_password or -var)"
  type        = string
  sensitive   = true
}
