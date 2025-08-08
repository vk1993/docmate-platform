#!/bin/bash

# Docker Build Script for DocMate Platform
# This script builds lightweight Docker images for all services

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
SERVICES=(
    "auth-service"
    "user-service"
    "admin-service"
    "appointment-service"
    "availability-service"
    "file-service"
    "notification-service"
    "payment-service"
    "prescription-service"
    "taxonomy-service"
    "gateway"
    "db-migration"
)

# Function to get dockerfile for image type
get_dockerfile() {
    case "$1" in
        "distroless")
            echo "Dockerfile.distroless"
            ;;
        "alpine")
            echo "Dockerfile.alpine"
            ;;
        "minimal")
            echo "Dockerfile.minimal"
            ;;
        "standard")
            echo "Dockerfile"
            ;;
        *)
            echo "Dockerfile.distroless"
            ;;
    esac
}

# Function to get size estimate for image type
get_size_estimate() {
    case "$1" in
        "distroless")
            echo "~100MB (Ultra-secure, no shell)"
            ;;
        "alpine")
            echo "~150MB (Small with shell access)"
            ;;
        "minimal")
            echo "~80MB (Custom JRE, smallest)"
            ;;
        "standard")
            echo "~300MB (Current version)"
            ;;
        *)
            echo "~100MB (Ultra-secure, no shell)"
            ;;
    esac
}

# Function to find project root directory
find_project_root() {
    local current_dir="$(pwd)"
    local check_dir="$current_dir"

    # Look for pom.xml in current directory and parent directories
    while [[ "$check_dir" != "/" ]]; do
        if [[ -f "$check_dir/pom.xml" ]]; then
            echo "$check_dir"
            return 0
        fi
        check_dir="$(dirname "$check_dir")"
    done

    # If not found, check if we're in docker subdirectory
    if [[ -f "../pom.xml" ]]; then
        echo "$(dirname "$current_dir")"
        return 0
    fi

    echo ""
    return 1
}

print_usage() {
    echo -e "${BLUE}DocMate Platform Docker Build Script${NC}"
    echo ""
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -t, --type TYPE     Image type: distroless, alpine, minimal, standard (default: distroless)"
    echo "  -s, --service NAME  Build specific service only"
    echo "  -p, --push          Push images to registry after build"
    echo "  -c, --clean         Clean build (no cache)"
    echo "  -h, --help          Show this help"
    echo ""
    echo "Image Types:"
    echo "  distroless: $(get_size_estimate "distroless")"
    echo "  alpine: $(get_size_estimate "alpine")"
    echo "  minimal: $(get_size_estimate "minimal")"
    echo "  standard: $(get_size_estimate "standard")"
    echo ""
    echo "Examples:"
    echo "  $0 -t distroless              # Build all services with distroless images"
    echo "  $0 -t alpine -s auth-service  # Build auth-service with alpine image"
    echo "  $0 -t minimal -c              # Clean build all services with minimal images"
    echo ""
    echo "Note: This script can be run from the project root or docker/ directory"
}

# Default values
IMAGE_TYPE="distroless"
SPECIFIC_SERVICE=""
PUSH_IMAGES=false
CLEAN_BUILD=false
REGISTRY="docmate"

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -t|--type)
            IMAGE_TYPE="$2"
            shift 2
            ;;
        -s|--service)
            SPECIFIC_SERVICE="$2"
            shift 2
            ;;
        -p|--push)
            PUSH_IMAGES=true
            shift
            ;;
        -c|--clean)
            CLEAN_BUILD=true
            shift
            ;;
        -h|--help)
            print_usage
            exit 0
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            print_usage
            exit 1
            ;;
    esac
done

# Validate image type
case "$IMAGE_TYPE" in
    "distroless"|"alpine"|"minimal"|"standard")
        ;;
    *)
        echo -e "${RED}Invalid image type: ${IMAGE_TYPE}${NC}"
        echo "Available types: distroless, alpine, minimal, standard"
        exit 1
        ;;
esac

DOCKERFILE=$(get_dockerfile "$IMAGE_TYPE")
SIZE_ESTIMATE=$(get_size_estimate "$IMAGE_TYPE")

echo -e "${BLUE}🚀 Building DocMate Platform with ${IMAGE_TYPE} images${NC}"
echo -e "${YELLOW}Expected size: ${SIZE_ESTIMATE}${NC}"
echo -e "${YELLOW}Using Dockerfile: ${DOCKERFILE}${NC}"
echo ""

# Find and change to project root directory
PROJECT_ROOT=$(find_project_root)
if [[ -z "$PROJECT_ROOT" ]]; then
    echo -e "${RED}Error: Cannot find project root directory with pom.xml file.${NC}"
    echo -e "${RED}Please ensure you're running this script from the project root or docker/ directory.${NC}"
    exit 1
fi

if [[ "$(pwd)" != "$PROJECT_ROOT" ]]; then
    echo -e "${YELLOW}Changing to project root directory: ${PROJECT_ROOT}${NC}"
    cd "$PROJECT_ROOT"
fi

# Build function
build_service() {
    local service=$1
    local start_time=$(date +%s)

    echo -e "${BLUE}Building ${service}...${NC}"

    # Build command
    local build_cmd="docker build"

    if [[ "$CLEAN_BUILD" == true ]]; then
        build_cmd="$build_cmd --no-cache"
    fi

    build_cmd="$build_cmd -f ${DOCKERFILE} --build-arg SERVICE_NAME=${service} -t ${REGISTRY}/${service}:${IMAGE_TYPE} -t ${REGISTRY}/${service}:latest ."

    echo -e "${YELLOW}Running: ${build_cmd}${NC}"

    if eval $build_cmd; then
        local end_time=$(date +%s)
        local duration=$((end_time - start_time))
        local size=$(docker images ${REGISTRY}/${service}:${IMAGE_TYPE} --format "table {{.Size}}" | tail -n 1)

        echo -e "${GREEN}✅ ${service} built successfully in ${duration}s (Size: ${size})${NC}"

        if [[ "$PUSH_IMAGES" == true ]]; then
            echo -e "${YELLOW}Pushing ${service}...${NC}"
            docker push ${REGISTRY}/${service}:${IMAGE_TYPE}
            docker push ${REGISTRY}/${service}:latest
        fi
    else
        echo -e "${RED}❌ Failed to build ${service}${NC}"
        return 1
    fi
}

# Check if the dockerfile exists
if [[ ! -f "$DOCKERFILE" ]]; then
    echo -e "${RED}Error: ${DOCKERFILE} not found in project root directory.${NC}"
    echo -e "${RED}Available files: $(ls -la Dockerfile* 2>/dev/null || echo 'No Dockerfile found')${NC}"
    exit 1
fi

# Main build process
if [[ -n "$SPECIFIC_SERVICE" ]]; then
    build_service "$SPECIFIC_SERVICE"
else
    echo -e "${YELLOW}Building all services...${NC}"

    for service in "${SERVICES[@]}"; do
        build_service "$service"
        echo ""
    done

    echo -e "${GREEN}🎉 All services built successfully!${NC}"
    echo ""
    echo -e "${BLUE}Image sizes:${NC}"
    docker images ${REGISTRY}/* --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}" | grep -E "(${IMAGE_TYPE}|latest)"
fi
