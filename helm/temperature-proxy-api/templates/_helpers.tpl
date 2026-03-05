{{/*
Expand the chart name.
*/}}
{{- define "temperature-proxy-api.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a fully-qualified name.
If Release.Name already contains the chart name, don't repeat it.
*/}}
{{- define "temperature-proxy-api.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Chart label (name + version).
*/}}
{{- define "temperature-proxy-api.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels — applied to every resource.
*/}}
{{- define "temperature-proxy-api.labels" -}}
helm.sh/chart: {{ include "temperature-proxy-api.chart" . }}
{{ include "temperature-proxy-api.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels — used in Deployment.spec.selector and Service.spec.selector.
Must be stable (never change after first deploy).
*/}}
{{- define "temperature-proxy-api.selectorLabels" -}}
app.kubernetes.io/name: {{ include "temperature-proxy-api.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}
