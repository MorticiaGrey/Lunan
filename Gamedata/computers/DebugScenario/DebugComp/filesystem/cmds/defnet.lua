local thisIp = lunan.getIp()
local allIp = lunan.getAllVisibleIps()
local netName = lunan.getCurrNetworkName()

print(netName .. ":")

print(htmlSpace .. "This: " .. thisIp)

for i = 1, tableLength(allIp), 1 do
    print(htmlSpace .. allIp[i])
end
