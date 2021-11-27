if args[1] == nil then
    print("usage: cd [DIRECTORY]")
    return
end
local path = lunan.getSelectedPath() .. args[1]
if not lunan.setSelectedPath(path) then
    print(args[1] .. ": No such directory")
end
