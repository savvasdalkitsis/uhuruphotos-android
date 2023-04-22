Get-ChildItem -Path ./ -Recurse -Filter *.kt | ForEach-Object {
    if (!(Select-String -Path $_.FullName -Pattern "Apache License")) {
        Get-Content -Path "LICENSE-HEADER", $_.FullName |
        Set-Content -Path "$($_.FullName).new"
        Move-Item -Path "$($_.FullName).new" -Destination $_.FullName -Force
    }
}