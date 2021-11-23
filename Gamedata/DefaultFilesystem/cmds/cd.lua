if args[1] == nil then
    return
end
local path = lunan.getSelectedPath() .. args[1]
if not lunan.setSelectedPath(path) then
    print(args[1] .. ": No such directory")
end